package de.mineking.discordutils.commands;

import de.mineking.discordutils.commands.condition.execution.IExecutionCondition;
import de.mineking.discordutils.commands.condition.registration.IRegistrationCondition;
import de.mineking.discordutils.commands.condition.registration.Scope;
import de.mineking.discordutils.commands.context.ContextBase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * A java representation of Discord's application commands that combines building and execution.
 *
 * @see #performCommand(ContextBase)
 * @see AnnotatedCommand
 */
public abstract class Command<C extends ContextBase<? extends GenericCommandInteractionEvent>> {
	public final String name;
	public final String description;

	public final CommandManager<C, ?> manager;
	private Command<C> parent;

	final Map<Object, Long> id = new HashMap<>();

	public final net.dv8tion.jda.api.interactions.commands.Command.Type type;

	/**
	 * The {@link IExecutionCondition} of this command. You can chain multiple conditions using {@link IExecutionCondition#and(IExecutionCondition)} and {@link IExecutionCondition#or(IExecutionCondition)}
	 */
	@Nullable
	protected IExecutionCondition<C> condition = null;

	/**
	 * The {@link IRegistrationCondition} of this command. You can chain multiple conditions using {@link IRegistrationCondition#and(IRegistrationCondition)} and {@link IRegistrationCondition#or(IRegistrationCondition)}
	 *
	 * @see IRegistrationCondition#scope(Scope)
	 */
	@Nullable
	protected IRegistrationCondition<C> registration = null;

	/**
	 * The options of this command. Only for {@link net.dv8tion.jda.api.interactions.commands.Command.Type#SLASH}
	 */
	@NotNull
	public final List<OptionData> options = new ArrayList<>();

	/**
	 * The subcommands of this command. Only for {@link net.dv8tion.jda.api.interactions.commands.Command.Type#SLASH}
	 */
	@NotNull
	public final Set<Command<C>> subcommands = new HashSet<>();

	/**
	 * Creates a new {@link Command} instance
	 *
	 * @param manager     The responsible {@link CommandManager}
	 * @param type        The {@link net.dv8tion.jda.api.interactions.commands.Command.Type} of this command
	 * @param name        The name of this command
	 * @param description The description of this command (only for {@link net.dv8tion.jda.api.interactions.commands.Command.Type#SLASH})
	 */
	public Command(@NotNull CommandManager<C, ?> manager, @NotNull net.dv8tion.jda.api.interactions.commands.Command.Type type, @NotNull String name, @NotNull String description) {
		Checks.notNull(manager, "manager");
		Checks.notNull(type, "type");
		Checks.notNull(name, "name");
		Checks.notNull(description, "description");

		this.manager = manager;
		this.type = type;
		this.name = name;
		this.description = description;
	}

	/**
	 * Creates a new {@link Command} instance
	 *
	 * @param manager The responsible {@link CommandManager}
	 * @param type    The {@link net.dv8tion.jda.api.interactions.commands.Command.Type} of this command
	 * @param name    The name of this command
	 */
	public Command(@NotNull CommandManager<C, ?> manager, @NotNull net.dv8tion.jda.api.interactions.commands.Command.Type type, @NotNull String name) {
		this(manager, type, name, "");
	}

	/**
	 * Creates a new {@link Command} instance of type {@link net.dv8tion.jda.api.interactions.commands.Command.Type#SLASH}
	 *
	 * @param manager     The responsible {@link CommandManager}
	 * @param name        The name of this command
	 * @param description The description of this command
	 */
	public Command(@NotNull CommandManager<C, ?> manager, @NotNull String name, @NotNull String description) {
		this(manager, net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH, name, description);
	}

	/**
	 * Creates a new {@link Command} instance of type {@link net.dv8tion.jda.api.interactions.commands.Command.Type#SLASH}
	 *
	 * @param manager The responsible {@link CommandManager}
	 * @param name    The name of this command
	 */
	public Command(@NotNull CommandManager<C, ?> manager, @NotNull String name) {
		this(manager, net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH, name, "");
	}

	/**
	 * This is executed every time users interact with this command
	 *
	 * @param context The {@link C} that holds information about this execution
	 */
	public abstract void performCommand(@NotNull C context) throws Exception;

	final void performCommand(@NotNull GenericCommandInteractionEvent event) throws Exception {
		var context = manager.createContext(event);
		if(getCondition().isAllowed(manager, context)) performCommand(context);
	}

	void forAll(Consumer<Command<C>> handler) {
		handler.accept(this);
		subcommands.forEach(handler);
	}

	/**
	 * @return An unmodifiable copy of this command's subcommands
	 */
	public Set<Command<C>> getSubcommands() {
		return Collections.unmodifiableSet(subcommands);
	}

	/**
	 * @return The parent of this command or {@code null}
	 */
	@Nullable
	public Command<C> getParent() {
		return parent;
	}

	/**
	 * @param guild The id of the guild to get the id of this command for. If the scope of this command is not {@link Scope#GUILD}, you can provide {@code null} here
	 * @return The id of this command or {@code null}
	 */
	@Nullable
	public Long getId(@NotNull Long guild) {
		return getRegistration().getScope() == Scope.GUILD
				? id.get(guild)
				: id.get(0);
	}

	/**
	 * @return The effective {@link IExecutionCondition} of this command
	 */
	@NotNull
	public IExecutionCondition<C> getCondition() {
		return condition != null ? condition : (parent != null ? parent.getCondition() : IExecutionCondition.always());
	}

	/**
	 * @return The effective {@link IRegistrationCondition} of this command
	 */
	@NotNull
	public IRegistrationCondition<C> getRegistration() {
		return registration != null ? registration : (parent != null ? parent.getRegistration() : IRegistrationCondition.always());
	}

	/**
	 * @param delimiter The string that is used as delimiter between command layers
	 * @return A string representing the command containing the name of this command and the name of it's parent, it's parent's parent, etc...
	 */
	@NotNull
	public String getPath(String delimiter) {
		return parent != null ? parent.getPath(delimiter) + delimiter + name : name;
	}

	/**
	 * @return The discord compatible string representation of this command's path. Because discord only supports three command layers, subcommands with more layers will be created as subcommands that contain {@code '_'} as delimiter.
	 */
	@NotNull
	public String getDiscordPath() {
		if(parent == null) return name;

		var path = parent.getDiscordPath();
		return path.split(" ").length > 2 ? path + "_" + name : path + " " + name;
	}

	/**
	 * Adds an option to this command
	 *
	 * @param option The option to add. If you want to use autocomplete, use {@link de.mineking.discordutils.commands.option.AutocompleteOption}
	 * @return {@code this}
	 */
	@NotNull
	public final Command<C> addOption(@NotNull OptionData option) {
		Checks.notNull(option, "option");

		options.add(option);
		return this;
	}

	/**
	 * Adds a subcommand. Only allowed for slash commands
	 *
	 * @param cmd The command implementation
	 * @return {@code this}
	 */
	@NotNull
	public final Command<C> addSubcommand(@NotNull Command<C> cmd) {
		Checks.notNull(name, "name");
		Checks.notNull(cmd, "cmd");

		if(type != net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH) throw new IllegalStateException();

		subcommands.add(cmd);
		cmd.parent = this;

		return this;
	}

	/**
	 * Adds a subcommand via annotations
	 *
	 * @param type The class to the command to register. Has to be annotated with {@link ApplicationCommand}
	 * @return {@code this}
	 */
	@NotNull
	public final <T> Command<C> addSubcommand(@NotNull Class<T> type) {
		Checks.notNull(type, "type");

		var instance = manager.createCommandInstance(type);
		return addSubcommand(AnnotatedCommand.getFromClass(manager, type, c -> Optional.ofNullable(instance), c -> Optional.ofNullable(instance)));
	}

	/**
	 * Builds this command for registration in discord
	 *
	 * @param guild The current guild or {@code null} if this is registered globally
	 * @return The resulting {@link CommandData}
	 */
	@NotNull
	public CommandData buildCommand(@Nullable Guild guild) {
		CommandData cmd;

		if(type != net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH) {
			cmd = Commands.context(type, name)
					.setDefaultPermissions(getRegistration().getPermission())
					.setGuildOnly(getRegistration().getScope() == Scope.GUILD_GLOBAL);
		} else {
			var sc = Commands.slash(name, "---")
					.addOptions(options)
					.setDefaultPermissions(getRegistration().getPermission())
					.setGuildOnly(getRegistration().getScope() == Scope.GUILD_GLOBAL);

			subcommands.forEach(group -> {
				if(!group.getRegistration().shouldRegister(manager, guild)) return;

				if(group.subcommands.isEmpty()) sc.addSubcommands(group.buildSubcommand(""));
				else {
					var gd = new SubcommandGroupData(group.name, "---")
							.addSubcommands(group.buildSubcommands("", guild));

					var localization = manager.manager.getLocalization(f -> f.getCommandPath(group), group.description);
					gd.setDescription(localization.defaultValue()).setDescriptionLocalizations(localization.values());

					sc.addSubcommandGroups(gd);
				}
			});

			cmd = sc;
		}

		var localization = manager.manager.getLocalization(f -> f.getCommandPath(this), description);
		if(cmd instanceof SlashCommandData sc) sc.setDescription(localization.defaultValue()).setDescriptionLocalizations(localization.values());
		else cmd.setNameLocalizations(localization.values());

		return cmd;
	}

	private SubcommandData buildSubcommand(String prefix) {
		var cmd = new SubcommandData(prefix + name, "---")
				.addOptions(options);

		var localization = manager.manager.getLocalization(f -> f.getCommandPath(this), description);
		cmd.setDescription(localization.defaultValue()).setDescriptionLocalizations(localization.values());

		return cmd;
	}

	private List<SubcommandData> buildSubcommands(String prefix, Guild guild) {
		var result = new ArrayList<SubcommandData>();

		subcommands.forEach(cmd -> {
			if(!cmd.getRegistration().shouldRegister(manager, guild)) return;

			if(cmd.subcommands.isEmpty()) result.add(cmd.buildSubcommand(prefix));
			else result.addAll(cmd.buildSubcommands(prefix + cmd.name + "_", guild));
		});

		return result;
	}
}
