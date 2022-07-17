package de.mineking.discord.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.Checks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;

import de.mineking.discord.Utils;
import de.mineking.discord.commands.history.ExecutionData;
import de.mineking.discord.commands.history.History;
import de.mineking.discord.commands.history.RuntimeData;
import de.mineking.discord.commands.interaction.Command;
import de.mineking.discord.commands.interaction.Feature;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.interaction.context.AutocompleteContext;
import de.mineking.discord.commands.interaction.context.CommandContext;
import de.mineking.discord.commands.interaction.handler.ButtonHandler;
import de.mineking.discord.commands.interaction.handler.InteractionHandler;
import de.mineking.discord.commands.interaction.handler.ModalHandler;
import de.mineking.discord.commands.interaction.handler.SelectHandler;
import de.mineking.discord.commands.interaction.option.AutocompleteOption;
import de.mineking.discord.commands.interaction.option.Choice;
import de.mineking.discord.commands.interaction.option.Option;
import de.mineking.discord.commands.list.Listable;
import de.mineking.discord.commands.localization.LocalizationMapper;

public class CommandManager extends ListenerAdapter {
	private final ExecutorService executor;
	
	private final ErrorMessageHandler errorHandler;
	private final CommandPermission everyonePermission; 
	
	
	private final BiPredicate<Guild, Feature> featureStateGetter;
	private final List<Feature> features;
	private final Map<String, ConsoleCommand> consoleCommands;
	
	
	private final LocalizationMapper localizationMapper;
	
	private final SlashCommand helpCommand;
	
	
	private final Predicate<RuntimeData> historyfilter;
	private final Consumer<RuntimeData> commandListener;	
	private final Map<Long, History> history;
	private final Integer maxHistoryLength;

	
	private Map<String, InteractionHandler<?, ?>> interactionHandlers;
	
	private Map<Long, Listable> lists;
	
	
	CommandManager(int threadPool, CommandPermission everyonePermission, ErrorMessageHandler errorHandler, BiPredicate<Guild, Feature> featureStateGetter, List<Feature> features, Map<String, ConsoleCommand> consoleCommands,
			LocalizationMapper localizationMapper, SlashCommand helpCommand,
			Predicate<RuntimeData> historyfilter, Consumer<RuntimeData> commandListener, Integer maxHistoryLength,
			Map<String, InteractionHandler<?, ?>> interactionHandlers) {
		this.errorHandler = errorHandler;
		this.everyonePermission = everyonePermission;
		
		this.featureStateGetter = featureStateGetter;
		this.features = features;
		this.consoleCommands = consoleCommands;
		
		this.localizationMapper = localizationMapper;
		this.helpCommand = helpCommand;
		
		this.historyfilter = historyfilter;
		this.commandListener = commandListener;
		this.history = new HashMap<>();
		this.maxHistoryLength = maxHistoryLength;
		
		this.lists = new HashMap<>();
		
		this.interactionHandlers = interactionHandlers;
		
		this.executor = Executors.newFixedThreadPool(threadPool, new ThreadFactory() {
			private int num;
			
			public ThreadFactory init() {
				num = 0;
				
				return this;
			}
			
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(() -> {
					r.run();
					
					num--;
				}, "CommandExecutor_" + (num++));
			}
		}.init());
		
		this.executor.execute(() -> {
			try(InputStreamReader isr = new InputStreamReader(System.in); BufferedReader br = new BufferedReader(isr)) {
				String line;
				
				while(true) {
					if((line = br.readLine()) != null) {
						try {
							performConsole(line);
						} catch(Exception e) {
							new Exception("Error handling console command", e).printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	
	/**
	 * @param userid
	 * 		The snowflake id of the user
	 * 
	 * @return The command history of this user
	 */
	@Nonnull
	public History getHistory(long userid) {
		if(!history.containsKey(userid)) {
			history.put(userid, new History(this));
		}
		
		return history.get(userid);
	}
	
	/**
	 * @return The featureStateGetter of this CommandManager
	 */
	@Nonnull
	public BiPredicate<Guild, Feature> getFeatureStateGetter() {
		return featureStateGetter;
	}
	
	/**
	 * @return A CommandPermission representing "everyone"
	 */
	@Nonnull
	public CommandPermission getEveryonePermission() {
		return everyonePermission;
	}
	
	/**
	 * @return The errorHandler
	 */
	@Nonnull
	public ErrorMessageHandler getErrorHandler() {
		return errorHandler;
	}
	
	/**
	 * @return All features added to this CommandManager
	 */
	@Nonnull
	public List<Feature> getFeatures() {
		return features;
	}
	
	/**
	 * @param name
	 * 		the feature's name
	 * 
	 * @return The feature with the specified name
	 */
	@Nonnull
	public Feature getFeature(@Nonnull String name) {
		Checks.notNull(name, "name");
		
		return features.stream()
				.filter(f -> f.getName().equals(name))
				.findFirst()
				.orElse(null);
	}

	/**
	 * @return All commands of the features added to this CommandManager
	 */
	@Nonnull
	public List<Command<?, ?>> getAllCommands() {
		return features.stream()
				.flatMap(f -> f.getCommands().values().stream())
				.toList();
	}
	
	@Nonnull
	public Command<?, ?> getCommand(@Nonnull String name) {
		Checks.notNull(name, "name");
		
		return features.stream()
				.flatMap(f -> f.getCommands().values().stream())
				.filter(c -> c.getName().equals(name))
				.findFirst()
				.orElse(null);
	}

	/**
	 * @return The default HelpCommand or {@code null} if none is set
	 */
	@Nullable
	public SlashCommand getHelpCommand() {
		return helpCommand;
	}
	
	/**
	 * @return The added localeMapper or {@code null} if none is set
	 */
	@Nullable
	public LocalizationMapper getLocalizationMapper() {
		return localizationMapper;
	}
	
	/**
	 * @return The maximum length of a users commands history before the first element will be deleted or {@code null} if the history can get infinite long
	 */
	public Integer getMaxHistoryLength() {
		return maxHistoryLength;
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
	 * @return The same CommandManager instance for chaining
	 */
	@Nonnull
	public CommandManager addInteractionHandler(@Nonnull String id, @Nonnull InteractionHandler<?, ?> handler) {
		Checks.notNull(id, "id");
		Checks.notNull(handler, "handler");
		
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
	 * Removes an interactionHandler
	 * 
	 * @param id
	 * 		The id the handler was added for
	 * 
	 * @return The previous interactionHandler
	 */
	@Nullable
	public InteractionHandler<?, ?> removeInteractionHandler(@Nonnull String id) {
		Checks.notNull(id, "id");
		
		return interactionHandlers.remove(id);
	}
	
	/**
	 * Performs a console command by name	
	 * @param command
	 * 		A string for execution. You can split arguments with space
	 * 
	 * @return true, if a command with the specific name was found 
	 */
	public boolean performConsole(@Nonnull String command) {	
		ConsoleCommand cmd;

		if((cmd = this.consoleCommands.get(command.split(" ")[0])) != null) {
			cmd.performCommand(command.split(" ")[0], command.split(" "));
			
			return true;
		}
		
		else {
			return false;
		}
	}
	
	private Command<?, ?> getCommandByPath(String p) {
		List<String> path = new LinkedList<>(Arrays.asList(p.split("/")));
		
		for(Feature f : features) {
			Command<?, ?> c;
			if((c = f.getCommands().get(path.get(0))) != null) {
				path.remove(0);
				
				if(c instanceof SlashCommand) {
					SlashCommand cmd = c.getAsSlash();
					
					while(true) {							
						if(!cmd.getSubcommands().isEmpty() && !path.isEmpty()) {
							if(cmd.getSubcommands().containsKey(path.get(0))) {
								cmd = cmd.getSubcommands().get(path.get(0));
								
								path.remove(0);
								
								continue;
							}
							
							else {
								break;
							}
						}
						
						if(cmd.getSubcommands().isEmpty()) {
							return cmd;
						}
					}
				}
				
				else {
					return c;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Performs a Command
	 * 
	 * @param data
	 * 		The RuntimeData for this execution
	 * 
	 * @param cmdData
	 * 		The CommandData to execute
	 * 
	 * @param addToHistory
	 * 		Whether this execution should be added to the executing users history
	 */
	public void performCommand(@Nonnull RuntimeData data, boolean addToHistory) {
		Checks.notNull(data, "data");
		
		Command<?, ?> c = getCommandByPath(data.path);
		
		execute(
				new ExecutionData<>(
						data, 
						c, 
						Utils.hasRole(data.member, c.getPermission().getRole(data.guild))
				), 
				c.getPermission(), 
				addToHistory
		);
	}
	
	private void execute(ExecutionData<?, ?> data, CommandPermission perm, boolean addToHistory) {
		if(!data.isPermitted()) {
			errorHandler.unlicensed(data, perm);
			
			return;
		}
		
		execute0(data);
		
		if(addToHistory) {
			if(historyfilter == null || historyfilter.test(data)) {
				addToHistory(data.member.getIdLong(), data);
			}
		}
		
		if(commandListener != null) {
			commandListener.accept(data);
		}
	}
	
	private <T extends GenericCommandInteractionEvent, C extends CommandContext<T>> void execute0(ExecutionData<T, C> data) {
		try {
			Command<T, C> cmd = data.getCommand();
			
			if(!data.event.isAcknowledged() && cmd.defaultAcknowledge() != null) {
				data.event.deferReply(cmd.defaultAcknowledge()).queue();
			}
			
			cmd.run(data);
		} catch(Exception e) {
			throw new RuntimeException("Execution of command " + data.getCommand().getPath(true) + " failed with error: ", e);
		}
	}
	
	
	/**
	 * Performs a Command asynchronously to the current thread and creates a new one in the execution thread poll instead
	 * 
	 * @param data
	 * 		The RuntimeData for this execution
	 * 
	 * @param cmd
	 * 		The CommandData to execute
	 * 
	 * @param addToHistory
	 * 		Whether to add this execution to the executing users history
	 * 
	 * @param before
	 * 		A task to execute before the command execution in the command thread
	 * 
	 * @param after
	 * 		A task to execute after the command execution in the command thread
	 */
	public void performCommandAsync(@Nonnull RuntimeData data, boolean addToHistory, @Nullable Runnable before, @Nullable Runnable after) {
		executor.execute(() -> {
			if(before != null) {
				before.run();
			}
			
			try {
				performCommand(data, addToHistory);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(after != null) {
				after.run();
			}
		});
	}
	
	@Override
	public void onGenericCommandInteraction(GenericCommandInteractionEvent event) {
		if(event.isFromGuild()) {
			performCommandAsync(new RuntimeData(event), true, null, null);
		}
	}
	
	@Override
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
		if(event.isFromGuild()) {
			Command<?, ?> cmd = getCommandByPath(event.getCommandPath());
			
			if(cmd != null) {
				if(cmd instanceof SlashCommand sc) {					
					for(Option o : sc.getOptions(event.getGuild())) {
						if(o.getName().equals(event.getFocusedOption().getName())) {
							if(o instanceof AutocompleteOption ao) {
								AutocompleteContext context = new AutocompleteContext(this, event);
								
								List<Choice> result = ao.handle(context);
								
								if(result != null) {
									event.replyChoices(
											result.stream()
											.map(c -> c.build(context))
											.toList()
									).queue();
									
									return;
								}
							}
							
							break;
						}
					}
				}
			}
		}
		
		event.replyChoices().queue();
	}
	
	/**
	 * Adds a entry to a members History
	 * 
	 * @param memberid
	 * 		The snowflake id of the member
	 * 
	 * @param cmd
	 * 		The ExecutionData of the command execution
	 * 
	 * @return The same CommandManager instance for chaining
	 */
	public CommandManager addToHistory(long memberid, @Nonnull ExecutionData<?, ?> cmd) {
		Checks.notNull(cmd, "cmd");
		
		if(!history.containsKey(memberid)) {
			history.put(memberid, new History(this));
		}
		
		history.get(memberid).add(cmd);
		
		return this;
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		if(event.isFromGuild() && event.getComponentId().startsWith("list") && lists.get(event.getMessage().getIdLong()) != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = new Gson().fromJson(
					event.getMessage().getActionRows().get(0)
						.getActionComponents().get(1)
						.getId(), 
					LinkedHashMap.class
			);
			
			int page = (int)(double)data.get("page");
			
			switch(event.getComponentId().split(":")[1]) {					
				case "back": page--; break;
				case "next": page++; break;
			}
			
			data.put("page", page);
			
			event.deferEdit().and(
				event.getHook().editOriginal(lists.get(event.getMessage().getIdLong())
						.buildMessage(
								event.getUserLocale(), 
								event.getGuild(), 
								event.getMember(), 
								data
						)
				)
			).queue();
		}
		
		else {
			for(String s : new LinkedHashMap<>(interactionHandlers).keySet()) {
				if(event.getComponentId().matches(s)) {
					InteractionHandler<?, ?> handler = interactionHandlers.get(s);
					
					if(handler instanceof ButtonHandler h) {
						if(h.execute(event)) {
							if(handler.autoRemove()) {
								interactionHandlers.remove(s);
							}
						}

						break;
					}
				}
			}
		}
	}
	
	@Override
	public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
		for(String s : new LinkedHashMap<>(interactionHandlers).keySet()) {
			if(event.getComponentId().matches(s)) {
				InteractionHandler<?, ?> handler = interactionHandlers.get(s);
				
				if(handler instanceof SelectHandler h) {
					if(h.execute(event)) {
						if(handler.autoRemove()) {
							interactionHandlers.remove(s);
						}
					}

					break;
				}
			}
		}
	}
	
	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		for(String s : new LinkedHashMap<>(interactionHandlers).keySet()) {
			if(event.getModalId().matches(s)) {
				InteractionHandler<?, ?> handler = interactionHandlers.get(s);
				
				if(handler instanceof ModalHandler h) {
					if(h.execute(event)) {
						if(handler.autoRemove()) {
							interactionHandlers.remove(s);
						}
					}

					break;
				}
			}
		}
	}

	/**
	 * Builds all commands added with their subcommands, description and options for the given guild.
	 * 
	 * @param guild
	 * 		The Guild you want to update the commands for
	 * 
	 * @return A CommandListUpdateAction representating this update. You have to execute this action by yourself!
	 */
	@Nonnull
	public CommandListUpdateAction updateCommands(@Nonnull Guild guild) {
		Checks.notNull(guild, "guild");
		
		return guild.updateCommands().addCommands(
				getAllCommands().stream()
					.filter(cmd -> cmd.getFeature().isEnabled(guild))
					.map(cmd -> {
						try {
							return cmd.build(guild);
						} catch(Exception e) {
							throw new RuntimeException("An error occoured when building command " + cmd.getName(), e);
						}
					})
					.toList()
		);
	}
	
	/**
	 * <i>Intern method; should only be used by people who know what they're doing</i>
	 */
	public void addList(long mesid, @Nonnull Listable obj) {
		Checks.notNull(obj, "obj");
		
		lists.put(mesid, obj);
	}
	
	/**
	 * <i>Intern method; should only be used by people who know what they're doing</i>
	 */
	public void removeList(long mesid) {
		lists.remove(mesid);
	}
}
