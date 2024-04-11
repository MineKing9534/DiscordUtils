package de.mineking.discordutils.commands;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.Manager;
import de.mineking.discordutils.commands.condition.Scope;
import de.mineking.discordutils.commands.context.IAutocompleteContext;
import de.mineking.discordutils.commands.context.ICommandContext;
import de.mineking.discordutils.commands.option.AutocompleteOption;
import de.mineking.discordutils.commands.option.IOptionParser;
import de.mineking.discordutils.events.EventManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @see #registerCommand(Class)
 * @see #registerCommand(Command)
 * @see #registerOptionParser(IOptionParser)
 */
public class CommandManager<C extends ICommandContext, A extends IAutocompleteContext> extends Manager {
	public final static Logger logger = LoggerFactory.getLogger(CommandManager.class);

	private final ExecutorService executor = Executors.newScheduledThreadPool(1);

	private final Function<GenericCommandInteractionEvent, ? extends C> contextCreator;
	private final Function<CommandAutoCompleteInteractionEvent, ? extends A> autocompleteContextCreator;

	private boolean autoUpdate = false;
	private Function<Guild, Cache> data;

	private final List<IOptionParser> optionParsers = new ArrayList<>();
	private final Map<String, Command<C>> commands = new HashMap<>();

	BiConsumer<GenericCommandInteractionEvent, CommandException> exceptionHandler;

	public CommandManager(@NotNull DiscordUtils.Builder<?> manager, @NotNull Function<GenericCommandInteractionEvent, ? extends C> contextCreator, @NotNull Function<CommandAutoCompleteInteractionEvent, ? extends A> autocompleteContextCreator) {
		Checks.notNull(contextCreator, "contextCreator");
		Checks.notNull(autocompleteContextCreator, "autocompleteContextCreator");

		this.contextCreator = contextCreator;
		this.autocompleteContextCreator = autocompleteContextCreator;

		registerOptionParser(IOptionParser.INTEGER);
		registerOptionParser(IOptionParser.LONG);
		registerOptionParser(IOptionParser.NUMBER);
		registerOptionParser(IOptionParser.BOOLEAN);
		registerOptionParser(IOptionParser.ROLE);
		registerOptionParser(IOptionParser.USER);
		registerOptionParser(IOptionParser.MEMBER);
		registerOptionParser(IOptionParser.CHANNEL);
		registerOptionParser(IOptionParser.MENTIONABLE);
		registerOptionParser(IOptionParser.ATTACHMENT);
		registerOptionParser(IOptionParser.STRING);
		registerOptionParser(IOptionParser.OPTIONAL);
		registerOptionParser(IOptionParser.ENUM);
		registerOptionParser(IOptionParser.ARRAY);
	}

	/**
	 * @param event The {@link GenericCommandInteractionEvent} to create the context for
	 * @return The resulting {@link C}
	 */
	@NotNull
	public C createContext(@NotNull GenericCommandInteractionEvent event) {
		Checks.notNull(event, "event");
		return contextCreator.apply(event);
	}

	/**
	 * @param event The {@link CommandAutoCompleteInteractionEvent} to create the context for
	 * @return The resulting {@link A}
	 */
	@NotNull
	public A createAutocompleteContext(@NotNull CommandAutoCompleteInteractionEvent event) {
		Checks.notNull(event, "event");
		return autocompleteContextCreator.apply(event);
	}

	/**
	 * @param exceptionHandler A consumer that will be called when something goes wrong executing a command
	 * @return {@code this}
	 */
	@NotNull
	public CommandManager<C, A> setExceptionHandler(BiConsumer<GenericCommandInteractionEvent, CommandException> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
		return this;
	}

	/**
	 * Registers a command
	 *
	 * @param command The {@link Command} implementation
	 * @return {@code this}
	 */
	@NotNull
	public CommandManager<C, A> registerCommand(@NotNull Command<C> command) {
		Checks.notNull(command, "command");

		commands.put(command.getDiscordPath(), command);
		command.register();

		return this;
	}

	/**
	 * Registers a command via annotations
	 *
	 * @param type                 The class to the command to register. If this class is not annotated with {@link ApplicationCommand}, methods with this annotation inside the class will be registered as commands
	 * @param instance             A function to provide the command instance
	 * @param autocompleteInstance A function to provide the command instance
	 * @return {@code this}
	 */
	@NotNull
	public <T> CommandManager<C, A> registerCommand(@NotNull Class<T> type, Function<C, Optional<T>> instance, Function<A, Optional<T>> autocompleteInstance) {
		Checks.notNull(type, "type");

		if(type.isAnnotationPresent(ApplicationCommand.class))
			registerCommand(AnnotatedCommand.getFromClass(this, type, instance, autocompleteInstance));
		else {
			var flag = false;

			for(var m : type.getMethods()) {
				if(!m.isAnnotationPresent(ApplicationCommand.class)) continue;
				flag = true;

				registerCommand(AnnotatedCommand.getFromMethod(this, type, m, instance, autocompleteInstance));
			}

			if(!flag)
				throw new IllegalArgumentException("Provided type is neither annotated with ApplicationCommand nor does it contain any methods with that annotation");
		}

		getManager().getManager(EventManager.class).ifPresent(eventManager -> eventManager.registerListener(type, () -> instance.apply(null).orElse(null)));

		return this;
	}

	/**
	 * Registers a command via annotations
	 *
	 * @param type The class to the command to register. If this class is not annotated with {@link ApplicationCommand}, methods with this annotation inside the class will be registered as commands
	 * @return {@code this}
	 */
	@NotNull
	public <T> CommandManager<C, A> registerCommand(@NotNull Class<T> type) {
		Checks.notNull(type, "type");

		var instance = createCommandInstance(type);
		return registerCommand(type, c -> Optional.ofNullable(instance), c -> Optional.ofNullable(instance));
	}

	/**
	 * Registers a {@link IOptionParser} that will can be used for custom options types.
	 *
	 * @param parser The parser to register
	 * @return {@code this}
	 * @see IOptionParser
	 */
	@NotNull
	public CommandManager<C, A> registerOptionParser(@NotNull IOptionParser parser) {
		Checks.notNull(parser, "parser");

		optionParsers.add(parser);
		return this;
	}

	/**
	 * Searches the registered {@link IOptionParser}s for a matching one to query the {@link OptionType} that should be used.
	 *
	 * @param type  The java type of the parameter
	 * @param param The java method parameter
	 * @return The {@link OptionType} that should be used. If no matching {@link IOptionParser} was found, {@link OptionType#UNKNOWN} will be returned.
	 */
	@NotNull
	public OptionType getOptionType(@NotNull Type type, @NotNull Parameter param) {
		Checks.notNull(type, "type");

		return optionParsers.stream().filter(p -> p.accepts(type, param)).map(p -> p.getType(this, type, param)).findFirst().orElse(OptionType.UNKNOWN);
	}

	/**
	 * Parses an option
	 *
	 * @param event The {@link GenericCommandInteractionEvent}
	 * @param name  The name of the option. This may be the same as the parameter name, but it is not required to!
	 * @param param The java method parameter
	 * @param type  The java type of the parameter
	 * @return The resulting option
	 */
	@Nullable
	public Object parseOption(@NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
		Checks.notNull(event, "event");
		Checks.notNull(name, "name");
		Checks.notNull(param, "param");
		Checks.notNull(type, "type");

		return optionParsers.stream().filter(p -> p.accepts(type, param)).map(p -> Optional.ofNullable(p.parse(this, event, name, param, type))).findFirst().flatMap(o -> o).orElse(null);
	}

	/**
	 * Calls the {@link IOptionParser#configure(Command, OptionData, Parameter, Type)} method to finalize an option's configuration
	 *
	 * @param command The {@link Command}
	 * @param option  The option to configure
	 * @param param   The java method parameter
	 * @param type    The java type of the parameter
	 * @return The resulting {@link OptionData}
	 */
	public OptionData configureOption(@NotNull Command<?> command, @NotNull OptionData option, @NotNull Parameter param, @NotNull Type type) {
		Checks.notNull(option, "option");
		Checks.notNull(param, "param");
		Checks.notNull(type, "type");

		return getParser(param, type).map(p -> p.configure(command, option, param, type)).orElse(option);
	}

	/**
	 * @param param The java method parameter
	 * @param type  The java type of this parameter
	 * @return An {@link Optional} containing the {@link IOptionParser} responsible for the specified {@link Parameter} if any is present.
	 */
	@NotNull
	public Optional<IOptionParser> getParser(@NotNull Parameter param, @NotNull Type type) {
		Checks.notNull(param, "param");

		return optionParsers.stream().filter(p -> p.accepts(type, param)).findFirst();
	}

	/**
	 * Creates an instance of on command
	 *
	 * @param type The java type of the command
	 * @return The resulting command instance. If an error occurs while creating the instance, {@code null} is returned.
	 */
	@Nullable
	public <T> T createCommandInstance(Class<T> type) {
		try {
			return getManager().createInstance(type, (i, p) -> null);
		} catch(Exception e) {
			logger.error("Failed to create command instance", e);
			return null;
		}
	}

	/**
	 * Automatically updates all commands. If the {@link JDA} is not yet ready, the update is scheduled, otherwise the update will be done immediately
	 *
	 * @param cache A {@link Cache} that is provided to all {@link #updateGuildCommands(Guild, Cache)} calls
	 * @return {@code this}
	 */
	public CommandManager<C, A> updateCommands(@Nullable Function<Guild, Cache> cache) {
		autoUpdate = true;
		this.data = cache;

		if(getManager().getJDA().getStatus() == JDA.Status.CONNECTED) {
			updateGlobalCommands().queue();
			getManager().getJDA().getGuilds().forEach(g -> updateGuildCommands(g).queue());
		}

		return this;
	}

	/**
	 * Automatically updates all commands. If the {@link JDA} is not yet ready, the update is scheduled, otherwise the update will be done immediately
	 *
	 * @return {@code this}
	 */
	public CommandManager<C, A> updateCommands() {
		return updateCommands(null);
	}

	/**
	 * @return A {@link Map} of all commands
	 */
	@NotNull
	public Map<String, Command<C>> getCommands() {
		return commands;
	}

	/**
	 * @param filter The {@link CommandFilter} to decide which commands to include
	 * @return A {@link Set} of commands matching the filter
	 */
	@NotNull
	public Set<Command<C>> findCommands(@NotNull CommandFilter<C> filter) {
		return commands.values().stream().filter(filter::filter).collect(Collectors.toSet());
	}

	/**
	 * @param type The java clazz of the annotated command to look up
	 * @return The command implementation for the provided class
	 */
	@NotNull
	public <T> AnnotatedCommand<T, C, A> getCommand(@NotNull Class<T> type) {
		Checks.notNull(type, "type");
		return commands.values().stream().filter(c -> c instanceof AnnotatedCommand<?, ?, ?> ac && ac.getClazz().equals(type)).findFirst().map(c -> (AnnotatedCommand<T, C, A>) c).orElseThrow();
	}

	/**
	 * Builds a {@link RestAction} that contains an update of all global commands
	 *
	 * @return The resulting {@link RestAction}
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public RestAction<List<net.dv8tion.jda.api.interactions.commands.Command>> updateGlobalCommands() {
		return getManager().getJDA().updateCommands().addCommands(findCommands(CommandFilter.all(CommandFilter.top(), (CommandFilter<C>) CommandFilter.scope(Scope.GUILD).invert())).stream().map(c -> c.buildCommand(null, new Cache())).toList()).onSuccess(commands -> commands.forEach(c -> this.commands.get(c.getName()).forAll(cmd -> cmd.id.put(0, c.getIdLong()))));
	}

	/**
	 * Builds a {@link RestAction}that contains an update of the commands of the provided {@link Guild}
	 *
	 * @param guild The guild to update the commands in
	 * @param data  A {@link Cache} holding information shared with all {@link de.mineking.discordutils.commands.condition.IRegistrationCondition}s
	 * @return The resulting {@link RestAction}
	 */
	@NotNull
	public RestAction<List<net.dv8tion.jda.api.interactions.commands.Command>> updateGuildCommands(@NotNull Guild guild, @NotNull Cache data) {
		Checks.notNull(guild, "guild");

		if(this.data != null) data.putAll(this.data.apply(guild).asMap());

		return guild.updateCommands().addCommands(findCommands(CommandFilter.all(CommandFilter.top(), CommandFilter.scope(Scope.GUILD), c -> c.getRegistration().shouldRegister(this, guild, data))).stream().map(c -> c.buildCommand(guild, data)).toList()).onSuccess(commands -> commands.forEach(c -> this.commands.get(c.getName()).forAll(cmd -> cmd.id.put(guild.getIdLong(), c.getIdLong()))));
	}

	/**
	 * Builds a {@link RestAction}that contains an update of the commands of the provided {@link Guild}
	 *
	 * @param guild The guild to update the commands in
	 * @return The resulting {@link RestAction}
	 */
	@NotNull
	public RestAction<List<net.dv8tion.jda.api.interactions.commands.Command>> updateGuildCommands(@NotNull Guild guild) {
		return updateGuildCommands(guild, new Cache());
	}

	@Override
	public void onGenericCommandInteraction(@NotNull GenericCommandInteractionEvent event) {
		var command = commands.get(event.getFullCommandName());
		if(command == null) return;

		executor.execute(() -> {
			try {
				command.performCommand(event);
			} catch(Exception e) {
				if(exceptionHandler != null) exceptionHandler.accept(event, new CommandException(command, e));
				logger.error("An error occurred whilst performing command", e);
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		if(!commands.containsKey(event.getFullCommandName())) return;

		for(var option : commands.get(event.getFullCommandName()).getOptions()) {
			if(option instanceof AutocompleteOption<?> && option.getName().equals(event.getFocusedOption().getName())) {
				((AutocompleteOption<A>) option).handleAutocomplete(createAutocompleteContext(event));
				return;
			}
		}
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		if(autoUpdate) updateGlobalCommands().queue();
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event) {
		if(autoUpdate) updateGuildCommands(event.getGuild()).queue();
	}
}
