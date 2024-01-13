package de.mineking.discordutils.commands;

import de.mineking.discordutils.commands.condition.ICommandPermission;
import de.mineking.discordutils.commands.condition.IExecutionCondition;
import de.mineking.discordutils.commands.condition.IRegistrationCondition;
import de.mineking.discordutils.commands.condition.Scope;
import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * A java representation of Discord's application commands that combines building and execution.
 *
 * @see #performCommand(ICommandContext)
 * @see AnnotatedCommand
 */
public abstract class Command<C extends ICommandContext> {
	@NotNull
	protected String name;
	@NotNull
	protected String description;

	@NotNull
	protected Scope scope = Scope.GLOBAL;

	private final CommandManager<C, ?> manager;
	private Command<C> parent;

	final Map<Object, Long> id = new HashMap<>();

	private final Type type;

	/**
	 * The {@link IExecutionCondition} of this command. You can chain multiple conditions using {@link IExecutionCondition#and(IExecutionCondition)} and {@link IExecutionCondition#or(IExecutionCondition)}
	 */
	@Nullable
	protected IExecutionCondition<C> condition = null;

	/**
	 * The {@link IRegistrationCondition} of this command. You can chain multiple conditions using {@link IRegistrationCondition#and(IRegistrationCondition)} and {@link IRegistrationCondition#or(IRegistrationCondition)}
	 */
	@Nullable
	protected IRegistrationCondition<C> registration = null;

	private final List<OptionData> options = new ArrayList<>();

	private final Set<Command<C>> subcommands = new HashSet<>();

	/**
	 * Creates a new {@link Command} instance
	 *
	 * @param manager     The responsible {@link CommandManager}
	 * @param type        The {@link Type} of this command
	 * @param name        The name of this command
	 * @param description The description of this command (only for {@link Type#SLASH})
	 */
	public Command(@NotNull CommandManager<C, ?> manager, @NotNull Type type, @NotNull String name, @NotNull String description) {
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
	 * @param type    The {@link Type} of this command
	 * @param name    The name of this command
	 */
	public Command(@NotNull CommandManager<C, ?> manager, @NotNull Type type, @NotNull String name) {
		this(manager, type, name, "");
	}

	/**
	 * Creates a new {@link Command} instance of type {@link Type#SLASH}
	 *
	 * @param manager     The responsible {@link CommandManager}
	 * @param name        The name of this command
	 * @param description The description of this command
	 */
	public Command(@NotNull CommandManager<C, ?> manager, @NotNull String name, @NotNull String description) {
		this(manager, Type.SLASH, name, description);
	}

	/**
	 * Creates a new {@link Command} instance of type {@link Type#SLASH}
	 *
	 * @param manager The responsible {@link CommandManager}
	 * @param name    The name of this command
	 */
	public Command(@NotNull CommandManager<C, ?> manager, @NotNull String name) {
		this(manager, Type.SLASH, name, "");
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

	protected void register() {
		subcommands.forEach(manager::registerCommand);
	}

	void forAll(Consumer<Command<C>> handler) {
		handler.accept(this);
		subcommands.forEach(handler);
	}

	@NotNull
	public CommandManager<C, ?> getManager() {
		return manager;
	}

	@NotNull
	public Type getType() {
		return type;
	}

	/**
	 * @return The name of this command
	 */
	@NotNull
	public String getName() {
		return name;
	}

	/**
	 * @return An unmodifiable copy of this command's subcommands. Only for {@link Type#SLASH}
	 */
	public Set<Command<C>> getSubcommands() {
		return Collections.unmodifiableSet(subcommands);
	}

	/**
	 * @return An unmodifiable copy of this command's options. Only for {@link Type#SLASH}
	 */
	@NotNull
	public List<OptionData> getOptions() {
		return Collections.unmodifiableList(options);
	}

	/**
	 * @return The parent of this command or {@code null}
	 */
	@Nullable
	public Command<C> getParent() {
		return parent;
	}

	/**
	 * @return The root of this command
	 */
	@NotNull
	public Command<C> getRoot() {
		return parent == null ? this : parent.getRoot();
	}

	/**
	 * @param guild The id of the guild to get the id of this command for. If the scope of this command is not {@link Scope#GUILD}, you can provide {@code 0} here
	 * @return The id of this command or {@code null}
	 */
	@Nullable
	public Long getId(long guild) {
		return getRoot().scope == Scope.GUILD ? id.get(guild) : id.get(0);
	}

	/**
	 * @return The id of this command
	 * @throws IllegalStateException If the scope of this command is {@link Scope#GUILD}
	 */
	@Nullable
	public Long getId() {
		if(getRoot().scope == Scope.GUILD) throw new IllegalStateException();
		return id.get(0);
	}

	/**
	 * @param guild The id of the guild to get the id of this command for. If the scope of this command is not {@link Scope#GUILD}, you can provide {@code 0} here
	 * @return A clickable mention of this command
	 * @throws IllegalStateException If the scope of this command is {@link Scope#GUILD}
	 */
	@NotNull
	public String getAsMention(long guild) {
		return "</" + getPath(" ") + ":" + getId(guild) + ">";
	}

	/**
	 * @return A clickable mention of this command
	 */
	@NotNull
	public String getAsMention() {
		if(getRoot().scope == Scope.GUILD) throw new IllegalStateException();
		return getAsMention(0);
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

	@NotNull
	public ICommandPermission<C> getPermission() {
		return getCondition().all().stream().filter(e -> e instanceof ICommandPermission<C>).map(e -> (ICommandPermission<C>) e).findFirst().orElse(new ICommandPermission<>() {
			@Override
			public boolean isAllowed(@NotNull CommandManager<C, ?> manager, @NotNull C context) {
				return true;
			}
		});
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
	 * @param data  The {@link Cache} to provide to {@link IRegistrationCondition}s
	 * @return The resulting {@link CommandData}
	 */
	@NotNull
	public CommandData buildCommand(@Nullable Guild guild, @NotNull Cache data) {
		CommandData cmd;

		if(type != net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH) {
			cmd = Commands.context(type, name).setDefaultPermissions(getPermission().requiredPermissions()).setGuildOnly(scope == Scope.GUILD_GLOBAL);
		} else {
			var sc = Commands.slash(name, "---");

			try {
				sc.addOptions(getOptions()).setDefaultPermissions(getPermission().requiredPermissions()).setGuildOnly(scope == Scope.GUILD_GLOBAL);

				subcommands.forEach(group -> {
					if(!group.getRegistration().shouldRegister(manager, guild, data)) return;

					if(group.subcommands.isEmpty()) sc.addSubcommands(group.buildSubcommand(""));
					else {
						var gd = new SubcommandGroupData(group.name, "---").addSubcommands(group.buildSubcommands("", guild, data));

						var localization = manager.getManager().getLocalization(f -> f.getCommandPath(group), group.description);
						gd.setDescription(localization.defaultValue()).setDescriptionLocalizations(localization.values());

						sc.addSubcommandGroups(gd);
					}
				});
			} catch(Exception e) {
				CommandManager.logger.error("Failed to configure command '{}'", name, e);
			}

			cmd = sc;
		}

		var localization = manager.getManager().getLocalization(f -> f.getCommandPath(this), description);
		if(cmd instanceof SlashCommandData sc && cmd.getType() == net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH)
			sc.setDescription(localization.defaultValue()).setDescriptionLocalizations(localization.values());
		else cmd.setNameLocalizations(localization.values());

		return cmd;
	}

	private SubcommandData buildSubcommand(String prefix) {
		var cmd = new SubcommandData(prefix + name, "---").addOptions(getOptions());

		var localization = manager.getManager().getLocalization(f -> f.getCommandPath(this), description);
		cmd.setDescription(localization.defaultValue()).setDescriptionLocalizations(localization.values());

		return cmd;
	}

	private List<SubcommandData> buildSubcommands(String prefix, Guild guild, Cache data) {
		var result = new ArrayList<SubcommandData>();

		subcommands.forEach(cmd -> {
			if(!cmd.getRegistration().shouldRegister(manager, guild, data)) return;

			if(cmd.subcommands.isEmpty()) result.add(cmd.buildSubcommand(prefix));
			else result.addAll(cmd.buildSubcommands(prefix + cmd.name + "_", guild, data));
		});

		return result;
	}
}
