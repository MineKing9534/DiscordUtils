package de.mineking.discordutils.help;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.Manager;
import de.mineking.discordutils.commands.CommandFilter;
import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.IAutocompleteContext;
import de.mineking.discordutils.commands.context.ICommandContext;
import de.mineking.discordutils.commands.option.AutocompleteOption;
import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.UIManager;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HelpManager<C extends ICommandContext> extends Manager {
	private OptionData targetOption = new AutocompleteOption<>(OptionType.STRING, "target", "target") {
		@Override
		public void handleAutocomplete(@NotNull IAutocompleteContext context) {
			context.getEvent().replyChoices(
					targets.stream()
							.filter(t -> t.matches(context.getEvent().getFocusedOption().getValue()))
							.filter(t -> t.isAvailable(context))
							.map(t -> new Command.Choice(t.getDisplay(context.getEvent().getUserLocale()), t.getKey()))
							.limit(OptionData.MAX_CHOICES)
							.toList()
			).queue();
		}
	};

	private final HelpTarget mainTarget;
	private final MessageMenu mainMenu;

	private final Map<String, MessageMenu> menus;
	private final List<? extends HelpTarget> targets;

	@SuppressWarnings("unchecked")
	public HelpManager(@NotNull DiscordUtils.Builder<?> manager, @NotNull Function<DiscordUtils.Builder<?>, List<? extends HelpTarget>> targets, @NotNull HelpTarget mainTarget) {
		Checks.notNull(manager, "manager");
		Checks.notNull(targets, "targets");
		Checks.notNull(mainTarget, "mainManager");

		this.targets = targets.apply(manager);

		this.mainTarget = mainTarget;
		var uiManager = manager.getManager(UIManager.class);

		this.mainMenu = uiManager.createMenu(
				"help",
				mainTarget::build,
				mainTarget.getComponents()
		);

		this.menus = this.targets.stream().collect(Collectors.toMap(HelpTarget::getKey, t -> uiManager.createMenu(
				"help." + t.getKey(),
				t::build,
				t.getComponents()
		)));

		var commandManager = (CommandManager<C, ?>) manager.getManager(CommandManager.class);
		commandManager.registerCommand(new HelpCommand<>(commandManager, this));

		commandManager.findCommands((CommandFilter<C>) CommandFilter.top().and(c -> !c.getSubcommands().isEmpty())).forEach(c -> c.addSubcommand(new HelpSubCommand<>(commandManager, this, c)));
	}

	/**
	 * @return The default {@link HelpTarget}
	 */
	@NotNull
	public HelpTarget getMainTarget() {
		return mainTarget;
	}

	/**
	 * @return The {@link OptionData} that is used as parameter
	 */
	@NotNull
	public OptionData getTargetOption() {
		return targetOption;
	}

	/**
	 * @param option The {@link OptionData} to use as parameter
	 * @return {@code this}
	 */
	@NotNull
	public HelpManager<C> setTargetOption(@NotNull OptionData option) {
		Checks.notNull(option, "option");
		this.targetOption = option;
		return this;
	}

	/**
	 * @param key The {@link HelpTarget}'s key
	 * @return An {@link Optional} holding the matching target
	 */
	@NotNull
	public Optional<? extends HelpTarget> getTarget(@Nullable String key) {
		if(key == null) return Optional.empty();

		return targets.stream()
				.filter(t -> t.getKey().equals(key))
				.findFirst();
	}

	/**
	 * @param key The {@link HelpTarget}'s key
	 * @return The {@link MessageMenu} for the provided key
	 */
	@NotNull
	public MessageMenu getMenu(@Nullable String key) {
		if(key == null) return mainMenu;
		return menus.getOrDefault(key, mainMenu);
	}

	/**
	 * @param key   The {@link HelpTarget}'s key
	 * @param event The {@link IReplyCallback} to reply the menu to
	 */
	public void display(@Nullable String key, @NotNull IReplyCallback event) {
		Checks.notNull(event, "event");

		getMenu(key).createState()
				.setState("target", key)
				.display(event);
	}
}
