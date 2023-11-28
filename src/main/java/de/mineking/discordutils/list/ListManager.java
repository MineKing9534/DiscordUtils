package de.mineking.discordutils.list;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.Manager;
import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.ICommandContext;
import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.UIManager;
import de.mineking.discordutils.ui.components.button.ButtonColor;
import de.mineking.discordutils.ui.components.button.ButtonComponent;
import de.mineking.discordutils.ui.components.button.label.TextLabel;
import de.mineking.discordutils.ui.components.types.ComponentRow;
import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.MessageSendState;
import de.mineking.discordutils.ui.state.SendState;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ListManager<C extends ICommandContext> extends Manager {
	private final UIManager uiManager;
	private final CommandManager<C, ?> commandManager;

	private OptionData pageOption = new OptionData(OptionType.INTEGER, "page", "Page")
			.setMinValue(1);

	@SuppressWarnings("unchecked")
	public ListManager(@NotNull DiscordUtils.Builder<?> manager) {
		uiManager = manager.getManager(UIManager.class);
		commandManager = manager.getManager(CommandManager.class);
	}

	/**
	 * @param option The {@link OptionData} for the page option
	 * @return {@code this}
	 */
	@NotNull
	public ListManager<C> setPageOption(@NotNull OptionData option) {
		this.pageOption = option;
		return this;
	}

	/**
	 * @param path                 The command path
	 * @param object               A function to provide the {@link Listable} for the current {@link DataState}
	 * @param additionalComponents Additional {@link ComponentRow}s to add to the menu
	 * @return The resulting menu
	 */
	@NotNull
	public <T extends ListEntry> MessageMenu createListMenu(@NotNull String path, @NotNull Function<DataState<MessageMenu>, Listable<T>> object, @NotNull ComponentRow... additionalComponents) {
		Checks.notNull(path, "path");
		Checks.notNull(object, "object");
		Checks.notNull(additionalComponents, "additionalComponents");


		var components = new ArrayList<ComponentRow>();

		components.add(ComponentRow.of(
				new ButtonComponent("first", ButtonColor.GRAY, Emoji.fromUnicode("⏪"))
						.appendHandler(s -> {
							s.setState("page", 1);
							s.update();
						}).asDisabled(s -> s.<Integer>getState("page") == 1),
				new ButtonComponent("back", ButtonColor.GRAY, Emoji.fromUnicode("⬅"))
						.appendHandler(s -> {
							s.<Integer>setState("page", p -> p - 1);
							s.update();
						}).asDisabled(s -> s.<Integer>getState("page") == 1),
				new ButtonComponent("page", ButtonColor.GRAY, (TextLabel) state -> "\uD83D\uDCD6 " + state.getState("page") + "/" + state.getCache("maxpage")).asDisabled(true),
				new ButtonComponent("next", ButtonColor.GRAY, Emoji.fromUnicode("➡"))
						.appendHandler(s -> {
							s.<Integer>setState("page", p -> p + 1);
							s.update();
						}).asDisabled(s -> s.getState("page") == s.getCache("maxpage")),
				new ButtonComponent("last", ButtonColor.GRAY, Emoji.fromUnicode("⏩"))
						.appendHandler(s -> {
							s.setState("page", s.getCache("maxpage"));
							s.update();
						}).asDisabled(s -> s.getState("page") == s.getCache("maxpage"))
		));
		components.addAll(Arrays.asList(additionalComponents));

		return uiManager.createMenu(
				"list." + path,
				s -> object.apply(s).buildEmbed(s, new ListContext(this, s.event)),
				components
		);
	}

	/**
	 * @param state                A consumer to configure the initial {@link SendState}
	 * @param object               A function to provide the {@link Listable} for the current {@link DataState}
	 * @param additionalComponents Additional {@link ComponentRow}s to add to the menu
	 * @return The resulting {@link ListCommand}
	 */
	@NotNull
	public <T extends ListEntry> ListCommand<C> createCommand(@NotNull BiConsumer<C, MessageSendState> state, @NotNull Function<DataState<MessageMenu>, Listable<T>> object, @NotNull ComponentRow... additionalComponents) {
		Checks.notNull(state, "state");
		Checks.notNull(object, "object");
		Checks.notNull(additionalComponents, "additionalComponents");

		return new ListCommand<>(
				path -> createListMenu(path, object, additionalComponents),
				state,
				commandManager,
				pageOption
		);
	}

	/**
	 * @param object               A function to provide the {@link Listable} for the current {@link DataState}
	 * @param additionalComponents Additional {@link ComponentRow}s to add to the menu
	 * @return The resulting {@link ListCommand}
	 */
	@NotNull
	public <T extends ListEntry> ListCommand<C> createCommand(@NotNull Function<DataState<MessageMenu>, Listable<T>> object, @NotNull ComponentRow... additionalComponents) {
		return createCommand((c, state) -> {}, object, additionalComponents);
	}
}
