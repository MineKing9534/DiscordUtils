package de.mineking.discord.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.history.RuntimeData;
import de.mineking.discord.commands.interaction.Command;
import de.mineking.discord.commands.interaction.Feature;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.interaction.context.CommandContext;
import de.mineking.discord.commands.interaction.handler.InteractionHandler;
import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;

public class CommandManagerBuilder {
	private int threadPool;
	
	private ErrorMessageHandler errorHandler;
	private CommandPermission everyonePermission;
	
	
	private BiPredicate<Guild, Feature> featureStateGetter;
	private Map<String, Consumer<Feature>> commands;
	private Map<String, Command<?, ?>> stdCommands;
	private Map<String, ConsoleCommand> consoleCommands;
	
	
	private Function<String, Map<DiscordLocale, String>> localeMapper;
	private DiscordLocale defaultLanguage;
	
	private SlashCommand helpCommand;
	
	
	private Predicate<RuntimeData> historyFilter;
	private Consumer<RuntimeData> commandListener;
	private Integer maxHistoryLength;
	
	private Map<String, InteractionHandler<?, ?>> interactionHandlers;
	
	
	
	private CommandManagerBuilder(int threadPool, CommandPermission everyonePermission, ErrorMessageHandler errorHandler, BiPredicate<Guild, Feature> featureStateGetter, 
			Map<String, Command<? ,?>> stdCommands, Map<String, Consumer<Feature>> commands, Map<String, ConsoleCommand> consoleCommands,
			Function<String, Map<DiscordLocale, String>> localeMapper, DiscordLocale defaultLanguage, SlashCommand helpCommand,
			Predicate<RuntimeData> historyfilter, Consumer<RuntimeData> commandListener, Integer maxHistoryLength,
			Map<String, InteractionHandler<?, ?>> interactionHandlers) {
		this.threadPool = threadPool;
		
		this.everyonePermission = everyonePermission;
		this.errorHandler = errorHandler;
		
		this.featureStateGetter = featureStateGetter;
		this.stdCommands = stdCommands;
		this.commands = commands;
		this.consoleCommands = consoleCommands;
		
		this.localeMapper = localeMapper;
		this.defaultLanguage = defaultLanguage;
		this.helpCommand = helpCommand;
		
		this.historyFilter = historyfilter;
		this.commandListener = commandListener;
		this.maxHistoryLength = maxHistoryLength;
		
		this.interactionHandlers = interactionHandlers;
	}


	/**
	 * @return
	 * 		A new CommandManagerBuilder with default configuration
	 */
	@Nonnull
	public static CommandManagerBuilder createDefault() {
		return new CommandManagerBuilder(10, new CommandPermission() {} , new ErrorMessageHandler() {}, (guild, feature) -> true, new LinkedHashMap<>(), new LinkedHashMap<>(), new HashMap<>(), null, null, null, null, null, 100, new HashMap<>());
	}

	/**
	 * @param threadPool
	 * 		The maximum number of threads to use for command execution. Default: 10
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setThreadPool(int threadPool) {
		this.threadPool = threadPool;
		
		return this;
	}

	/**
	 * @param everyonePermission
	 * 		A CommandPermission representing "everyone"
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setEveryonePermission(@Nonnull CommandPermission everyonePermission) {
		Checks.nonNull(everyonePermission, "everyonePermission");
		
		this.everyonePermission = everyonePermission;
		
		return this;
	}
	
	/**
	 * @param errorHandler
	 * 		The ErrorMessageHandler to handle error during execution. This is not a exception handler but a handler for custom errors from the DiscordUtils library!
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setErrorHandler(@Nonnull ErrorMessageHandler errorHandler) {
		Checks.nonNull(errorHandler, "errorHandler");
		
		this.errorHandler = errorHandler;
		
		return this;
	}
	
	/**
	 * @param featureStateGetter
	 * 		A Function to check whether a feature is enabled for a guild. The default will always return true!
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setFeatureStateGetter(@Nonnull BiPredicate<Guild, Feature> featureStateGetter) {
		Checks.nonNull(featureStateGetter, "featureStateGetter");
		
		this.featureStateGetter = featureStateGetter;
		
		return this;
	}
	
	/**
	 * Creates a new feature
	 * 
	 * @param name
	 * 		The feature's name
	 * 
	 * @param handler
	 * 		A Function to configure a feature after creation
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder createFeature(@Nonnull String name, @Nonnull Consumer<Feature> handler) {
		Checks.nonNull(name, "name");
		Checks.nonNull(handler, "handler");
		
		commands.put(name, handler);
		
		return this;
	}
	
	/**
	 * Registers a new command to the std feature
	 * 
	 * @param name
	 * 		The Command's name
	 * 
	 * @param cmd
	 * 		The Command
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public <T extends GenericCommandInteractionEvent, C extends CommandContext<T>> CommandManagerBuilder registerCommand(@Nonnull String name, @Nonnull Command<T, C> cmd) {
		Checks.nonNull(name, "name");
		Checks.nonNull(cmd, "cmd");
		
		stdCommands.put(name, cmd);
		
		return this;
	}

	/**
	 * Registers a new ConsoleCommand that can be used for controlling the bot via console
	 * 
	 * @param name
	 * 		The commands name
	 * 
	 * @param cmd
	 * 		The ConsoleCommand
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder registerConsoleCommand(@Nonnull String name, @Nonnull ConsoleCommand cmd) {
		Checks.nonNull(name, "name");
		Checks.nonNull(cmd, "cmd");
		
		this.consoleCommands.put(name, cmd);
		
		return this;
	}

	/**
	 * @param localeMapper
	 * 		A function to map from intern description names to actual localized descriptions or {@code null} if you don't want to use localization.
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setLocaleMapper(@Nullable Function<String, Map<DiscordLocale, String>> localeMapper) {
		this.localeMapper = localeMapper;
		
		return this;
	}

	/**
	 * @param defaultLanguage
	 * 		The default locale or {@code null} if you don't want to set a default locale. The description will be set to the result of the localeMapper for this locale as default. This will be shown to the client if the user doesn't have a supported language selected.
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setDefaultLanguage(@Nullable DiscordLocale defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
		
		return this;
	}

	/**
	 * @param helpCommand
	 * 		A SlashCommand to add to all commands as subcommand or {@code null} if you don't want to use a help-subcommand. The executing super-command's name can be found out by using `super.getPath()`.
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setHelpCommand(@Nullable SlashCommand helpCommand) {
		this.helpCommand = helpCommand;
		
		return this;
	}

	/**
	 * @param historyfilter
	 * 		A Predicate to decide whether to add a command to a users history or not or {@code null} to add all executions.
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setHistoryFilter(@Nullable Predicate<RuntimeData> historyFilter) {
		this.historyFilter = historyFilter;
		
		return this;
	}

	/**
	 * @param commandListener
	 * 		A function that will be called on every command execution.
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setCommandListener(@Nullable Consumer<RuntimeData> commandListener) {
		this.commandListener = commandListener;
		
		return this;
	}

	/**
	 * @param maxHistoryLength
	 * 		The maximum length of a users command history before removing the first element or  {@code null} to set no limit. Default: 100
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder setMaxHistoryLength(@Nullable Integer maxHistoryLength) {
		this.maxHistoryLength = maxHistoryLength;
		
		return this;
	}

	/**
	 * Adds a new interaction handler
	 * 
	 * @param id
	 * 		A regex string for matching
	 * 
	 * @param handler
	 * 		The handler
	 * 
	 * @return the same CommandManagerBuilder instance
	 */
	@Nonnull
	public CommandManagerBuilder addInteractionHandler(@Nonnull String id, @Nonnull InteractionHandler<?, ?> handler) {
		Checks.nonNull(id, "id");
		Checks.nonNull(handler, "handler");
		
		interactionHandlers.put(id, handler);
		
		if(handler.autoRemove()) {
			ScheduledExecutorService e = Executors.newSingleThreadScheduledExecutor();
			
			e.schedule(() -> {
				if(interactionHandlers.get(id) != null && interactionHandlers.get(id).equals(handler)) {
					interactionHandlers.remove(id);
				}
			}, 10, TimeUnit.MINUTES);
			
			e.shutdown();
		}
		
		return this;
	}

	
	/**
	 * Builds a CommandManaher based on the current configuration
	 * 
	 * @return The resulting CommandManager
	 */
	@Nonnull
	public CommandManager build() {
		List<Feature> features = new ArrayList<>();
	
		CommandManager cmdMan = new CommandManager(threadPool, everyonePermission, errorHandler, featureStateGetter, features, consoleCommands, localeMapper, defaultLanguage, helpCommand, historyFilter, commandListener, maxHistoryLength, interactionHandlers);
	
		Feature std = new Feature("std", cmdMan);
		
		for(var c : stdCommands.entrySet()) {
			std.addCommand(c.getKey(), c.getValue());
		}
		
		features.add(std);
		
		for(var f : commands.entrySet()) {
			Feature feature = new Feature(f.getKey(), cmdMan);
			
			f.getValue().accept(feature);
			
			features.add(feature);
		}
		
		return cmdMan;
	}
}
