package de.mineking.discord.commands;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import de.mineking.discord.commands.annotated.*;
import de.mineking.discord.commands.annotated.option.Option;
import de.mineking.discord.commands.exception.CommandExceptionHandler;
import de.mineking.discord.commands.exception.CommandExecutionException;
import de.mineking.discord.commands.inherited.BaseCommand;
import de.mineking.discord.events.Listener;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandManager<C extends ContextBase> extends Module {
	private final static Logger logger = LoggerFactory.getLogger(CommandManager.class);

	private final ContextCreator<C> context;
	private final Map<String, CommandImplementation> commands = new ConcurrentHashMap<>();
	private final Set<Object> externalOptions = new HashSet<>();

	private CommandExceptionHandler exceptionHandler;

	public final ExecutorService executor = Executors.newCachedThreadPool(r -> new Thread(r, "CommandManager"));

	public CommandManager(DiscordUtils manager, ContextCreator<C> context) {
		super(manager);

		this.context = context;
	}

	@Override
	public void cleanup() {
		executor.shutdownNow();
	}

	public CommandManager<C> setExceptionHandler(CommandExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
		return this;
	}

	public Map<String, CommandImplementation> getCommands() {
		return Collections.unmodifiableMap(commands);
	}

	public Set<CommandImplementation> findCommands(CommandFilter filter) {
		return commands.values().stream()
				.filter(cmd -> filter.test(this, cmd))
				.collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	public <T> T getExternalOption(Class<T> id) {
		for(var o : externalOptions)
			if(o.getClass().equals(id)) return (T) o;

		return null;
	}

	public ContextCreator<C> getContext() {
		return context;
	}

	@Override
	public void onGenericCommandInteraction(@NotNull GenericCommandInteractionEvent event) {
		System.out.println("CommandEvent: '" + event.getFullCommandName() + "'");

		executor.execute(() ->
				Optional.ofNullable(commands.get(event.getFullCommandName())).ifPresent(c -> {
					try {
						System.out.println("Found matching implementation: " + c.getPath("."));
						c.handle(event);
					} catch(Exception e) {
						if(exceptionHandler != null) {
							exceptionHandler.handleException(c, e, event);
						}

						if(e instanceof CommandExecutionException) logger.error(e.getMessage(), e.getCause());
						else logger.error("Something went wrong when executing command", e);
					}
				})
		);
	}

	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		Optional.ofNullable(commands.get(event.getFullCommandName())).ifPresent(c -> {
			try {
				c.handleAutocomplete(event);
			} catch(CommandExecutionException e) {
				logger.error(e.getMessage(), e.getCause());
			} catch(Exception e) {
				logger.error("Something went wrong when executing command", e);
			}
		});
	}

	public <O> CommandManager<C> registerExternalOption(O instance) {
		if(!instance.getClass().isAnnotationPresent(Option.class)) throw new IllegalArgumentException(instance.getClass().getName() + ": " + Stream.of(instance.getClass().getAnnotations()).map(a -> a.getClass().getName()).collect(Collectors.joining("; ")));

		externalOptions.add(instance);

		return this;
	}

	public CommandImplementation registerCommand(Class<?> command) {
		return registerCommand(null, command);
	}

	public CommandImplementation registerCommand(CommandImplementation parent, Class<?> command) {
		return registerCommand(parent, createCommandInstance(command));
	}

	public CommandImplementation registerCommand(Object command) {
		return registerCommand(null, command);
	}

	public CommandImplementation registerCommand(CommandImplementation parent, Object command) {
		var type = command.getClass();
		var info = type.getAnnotation(ApplicationCommand.class);

		if(info == null) throw new IllegalArgumentException();

		var subcommands = Stream.concat(
				Stream.of(type.getClasses()),
				Stream.of(info.subcommands())
		).toArray(Class<?>[]::new);

		var impl = findImplementation(parent, new HashSet<>(), command, type, info);

		if(parent != null) parent.children.add(impl);

		for(var sub : subcommands) {
			var subInfo = sub.getAnnotation(ApplicationCommand.class);

			if(subInfo != null) registerCommand(impl, sub);
		}

		commands.put((parent == null || info.type() != Command.Type.SLASH ? "" : (parent.getPath() + " ")) + info.name(), impl);

		var instance = impl.instance.apply(null);

		for(var m : type.getMethods()) {
			if(m.isAnnotationPresent(Setup.class)) {
				var params = new Object[m.getParameterCount()];

				for(int i = 0; i < m.getParameterCount(); i++) {
					var param = m.getParameters()[i];

					if(param.getType().isAssignableFrom(CommandManager.class)) params[i] = this;
					else if(param.getType().isAssignableFrom(DiscordUtils.class)) params[i] = manager;
				}

				try {
					m.invoke(instance, params);
				} catch(IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException("Failed to initialize command", e);
				}
			} else if(m.isAnnotationPresent(Listener.class)) {
				var eventManager = manager.getEventManager();

				if(eventManager == null) {
					logger.warn("Couldn't register listener in command because no EventManager is present in it's parent DiscordUtils");
					continue;
				}

				try {
					var listener = m.getAnnotation(Listener.class);
					var handler = listener.type().getConstructor(String.class, Consumer.class).newInstance(
							listener.filter(),
							(Consumer<?>) event -> {
								var params = new Object[m.getParameterCount()];

								for(int i = 0; i < m.getParameterCount(); i++) {
									var param = m.getParameters()[i];

									if(param.getType().isAssignableFrom(CommandManager.class)) params[i] = this;
									else if(param.getType().isAssignableFrom(DiscordUtils.class)) params[i] = manager;
									else if(param.getType().isAssignableFrom(event.getClass())) params[i] = event;
								}

								try {
									m.invoke(instance, params);
								} catch(IllegalAccessException | InvocationTargetException e) {
									logger.error("Failed to call listener in command", e);
								}
							}
					);

					eventManager.registerHandler(handler);
				} catch(Exception e) {
					logger.error("Failed to register listener in command", e);
				}
			}
		}

		return impl;
	}

	public CommandManager<C> registerAllCommands(String commandsPackage) {
		return registerAllCommands(commandsPackage, x -> true);
	}

	public CommandManager<C> registerAllCommands(String commandsPackage, Predicate<ApplicationCommand> filter) {
		var temp = new Reflections(commandsPackage).getTypesAnnotatedWith(ApplicationCommand.class).stream()
				.filter(c -> c.getNestHost().equals(c))
				.toList();

		var exclude = temp.stream()
				.flatMap(c -> Arrays.stream(c.getAnnotation(ApplicationCommand.class).subcommands()))
				.toList();

		temp.stream()
				.filter(exclude::contains)
				.filter(c -> filter.test(c.getAnnotation(ApplicationCommand.class)))
				.forEach(this::registerCommand);

		return this;
	}

	public CommandManager<C> registerCommand(String name, BaseCommand<C> command) {
		var temp = name.split(" ");

		if(temp.length == 1) commands.putAll(command.build(this, name));
		else {
			var cmdName = temp[temp.length - 1];
			var parent = commands.get(name.substring(0, name.length() - cmdName.length() - 1));

			if(parent == null) throw new IllegalArgumentException("The parent command could not be found! (" + name + ")");

			var impl = command.build(this, parent, cmdName);
			parent.children.add(impl.get(name));
			commands.putAll(impl);
		}

		return this;
	}

	public CommandManager<C> registerCommand(String name, CommandImplementation impl) {
		commands.put(name, impl);

		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> CommandManager<C> registerCommands(Class<T> type, Function<C, T> creator) {
		for(var m : type.getMethods()) {
			var cmd = m.getAnnotation(ApplicationCommand.class);

			if(cmd != null) {
				commands.put(cmd.name(), new ReflectionCommandImplementation(this, null, Collections.emptySet(), CommandInfo.ofAnnotation(cmd), type,
						ctx -> ctx != null ? creator.apply((C) ctx) : new Object(),
						m
				));
			}
		}

		return this;
	}

	protected Object createCommandInstance(Class<?> command) {
		try {
			for(var constructor : command.getConstructors()) {
				if(constructor.getParameterCount() == 0) return constructor.newInstance();
				else if(constructor.isAnnotationPresent(CommandConstructor.class)) {
					var params = new Object[constructor.getParameterCount()];

					for(int i = 0; i < constructor.getParameterCount(); i++) {
						var param = constructor.getParameters()[i];

						if(param.getType().isAssignableFrom(CommandManager.class)) {
							params[i] = this;
						} else if(param.getType().isAssignableFrom(DiscordUtils.class)) {
							params[i] = manager;
						}
					}

					return constructor.newInstance(params);
				}
			}

			throw new IllegalArgumentException("Cannot create instance for command '" + command.getName() + "'");
		} catch(Exception e) {
			throw new RuntimeException("Failed to instantiate command", e);
		}
	}

	public CommandImplementation findImplementation(CommandImplementation parent, Set<CommandImplementation> children, Object instance, Class<?> type, ApplicationCommand info) {
		for(var m : type.getMethods())
			if(m.isAnnotationPresent(ApplicationCommandMethod.class))
				return new ReflectionCommandImplementation(this, parent, children, CommandInfo.ofAnnotation(info), type, ctx -> instance, m);

		return new ReflectionCommandImplementationBase(parent, children, CommandInfo.ofAnnotation(info), type, ctx -> instance);
	}

	public CommandListUpdateAction updateCommands() {
		return manager.getJDA().updateCommands()
				.addCommands(
						findCommands(CommandFilter.TOP).stream()
								.map(i -> i.build(this))
								.toList()
				);
	}
}
