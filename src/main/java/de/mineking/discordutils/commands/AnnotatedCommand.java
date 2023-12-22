package de.mineking.discordutils.commands;

import de.mineking.discordutils.commands.condition.IExecutionCondition;
import de.mineking.discordutils.commands.condition.IRegistrationCondition;
import de.mineking.discordutils.commands.condition.cooldown.Cooldown;
import de.mineking.discordutils.commands.condition.cooldown.CooldownImpl;
import de.mineking.discordutils.commands.condition.cooldown.CooldownPool;
import de.mineking.discordutils.commands.context.IAutocompleteContext;
import de.mineking.discordutils.commands.context.ICommandContext;
import de.mineking.discordutils.commands.option.Autocomplete;
import de.mineking.discordutils.commands.option.AutocompleteOption;
import de.mineking.discordutils.commands.option.Choice;
import de.mineking.discordutils.commands.option.Option;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * An implementation of {@link Command} that uses annotations to build and run the command.
 *
 * @param <C> The context type
 * @see Command
 * @see ApplicationCommand
 * @see ApplicationCommandMethod
 * @see CommandManager#registerCommand(Class)
 */
public class AnnotatedCommand<T, C extends ICommandContext, A extends IAutocompleteContext> extends Command<C> {
	private final static Map<String, CooldownImpl<?>> cooldowns = new HashMap<>();

	@NotNull
	public final Class<T> clazz;
	@NotNull
	public final ApplicationCommand info;
	@NotNull
	public final Function<C, Optional<T>> instance;
	@NotNull
	public final Function<A, Optional<T>> autocompleteInstance;

	private final Method method;

	@SuppressWarnings("unchecked")
	private AnnotatedCommand(CommandManager<C, ?> manager, ApplicationCommand info, Class<T> clazz, Method method, @NotNull Function<C, Optional<T>> instance, @NotNull Function<A, Optional<T>> autocompleteInstance) {
		super(manager, info.type(), info.name(), info.description());

		this.clazz = clazz;
		this.method = method;
		this.info = info;

		this.instance = instance;
		this.autocompleteInstance = autocompleteInstance;

		this.scope = info.scope();

		for(var m : clazz.getMethods()) {
			if(!m.isAnnotationPresent(Setup.class)) continue;

			try {
				manager.getManager().invokeMethod(m, null, (i, p) -> {
					if(p.getType().isAssignableFrom(AnnotatedCommand.class)) return this;
					else return null;
				});
			} catch(Exception e) {
				CommandManager.logger.error("Failed to execute setup method {} on {}", m.getName(), name);
			}
		}

		if(method != null) {
			var autocomplete = new HashMap<String, Method>();

			for(var m : clazz.getMethods()) {
				if(m.isAnnotationPresent(Autocomplete.class)) {
					autocomplete.put(m.getAnnotation(Autocomplete.class).value(), m);
				}
			}

			var choices = new HashMap<String, Field>();

			for(var f : clazz.getFields()) {
				try {
					if(f.isAnnotationPresent(Choice.class)) choices.put(f.getAnnotation(Choice.class).value(), f);
				} catch(Exception e) {
					CommandManager.logger.error("Failed to read choice field", e);
				}
			}

			for(int i = 0; i < method.getParameterCount(); i++) {
				var p = method.getParameters()[i];
				var generic = method.getGenericParameterTypes()[i];

				var o = p.getAnnotation(Option.class);
				if(o == null) continue;

				var name = getOptionName(p);

				manager.getParser(p).ifPresent(op -> op.registerOption(this, buildOption(o, p, generic, name, autocomplete.get(o.id().isEmpty() ? name : o.id()), choices.get(o.id().isEmpty() ? name : o.id())), p));
			}

			var tInstance = instance.apply(null).orElse(null);
			for(var f : clazz.getFields()) {
				try {
					if(IExecutionCondition.class.isAssignableFrom(f.getType())) condition = getCondition().and((IExecutionCondition<C>) f.get(tInstance));
					else if(IRegistrationCondition.class.isAssignableFrom(f.getType())) registration = getRegistration().and((IRegistrationCondition<C>) f.get(tInstance));
				} catch(Exception e) {
					CommandManager.logger.error("Failed to read condition field '{}' for command '{}'", f.getName(), name, e);
				}
			}
		}

		for(var m : clazz.getMethods()) {
			var cooldown = m.getAnnotation(Cooldown.class);

			if(cooldown != null) {
				var impl = new CooldownImpl<C>(Duration.ofMillis(cooldown.unit().toMillis(cooldown.interval())), cooldown.uses(), (man, context) ->
						instance.apply(context).ifPresent(i -> {
							try {
								manager.getManager().invokeMethod(m, i, (x, p) -> {
									if(p.getType().isAssignableFrom(context.getClass())) return context;
									else return null;
								});
							} catch(InvocationTargetException | IllegalAccessException e) {
								CommandManager.logger.error("Failed to execute cooldown error method", e);
							}
						})
				);

				if(!cooldown.identifier().isEmpty()) cooldowns.put(cooldown.identifier(), impl);

				condition = getCondition().and(impl);

				break;
			}
		}

		if(clazz.isAnnotationPresent(CooldownPool.class)) {
			var impl = cooldowns.get(clazz.getAnnotation(CooldownPool.class).value());
			if(impl == null) CommandManager.logger.warn("Cooldown-Pool referenced by " + getPath(".") + " not found - Ignoring...");
			else condition = getCondition().and((CooldownImpl<C>) impl);
		}

		if(info.type() == net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH) {
			for(var t : clazz.getClasses()) {
				var i = t.getAnnotation(ApplicationCommand.class);

				if(i == null || i.type() != net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH) continue;

				addSubcommand(createCommand(t, manager));
			}
		}

		if(getSubcommands().isEmpty() && method == null) CommandManager.logger.warn("Command '{}' has neither subcommands nor a method", name);
	}

	private static <T, C extends ICommandContext, A extends IAutocompleteContext> AnnotatedCommand<T, C, A> createCommand(Class<T> type, CommandManager<C, A> manager) {
		var instance = manager.createCommandInstance(type);
		return getFromClass(manager, type, x -> Optional.ofNullable(instance), x -> Optional.ofNullable(instance));
	}

	/**
	 * Creates an instance of {@link AnnotatedCommand}
	 *
	 * @param manager              The responsible {@link CommandManager}
	 * @param clazz                The java type of the command
	 * @param instance             A function to provide the instance from {@link C}
	 * @param autocompleteInstance A function to provide the instance from {@link A}
	 * @return The resulting {@link Command} instance
	 */
	public static <T, C extends ICommandContext, A extends IAutocompleteContext> AnnotatedCommand<T, C, A> getFromClass(@NotNull CommandManager<C, A> manager,
	                                                                                                                    @NotNull Class<T> clazz,
	                                                                                                                    @NotNull Function<C, Optional<T>> instance,
	                                                                                                                    @NotNull Function<A, Optional<T>> autocompleteInstance) {
		Checks.notNull(manager, "manager");
		Checks.notNull(clazz, "clazz");
		Checks.notNull(instance, "instance");
		Checks.notNull(autocompleteInstance, "autocompleteInstance");

		var info = clazz.getAnnotation(ApplicationCommand.class);
		if(info == null) throw new IllegalArgumentException("The provided class is not annotated with ApplicationCommand");

		Method method = null;

		for(var m : clazz.getMethods()) {
			if(m.isAnnotationPresent(ApplicationCommandMethod.class)) {
				method = m;
				break;
			}
		}

		return new AnnotatedCommand<>(manager, info, clazz, method, instance, autocompleteInstance);
	}

	public static <T, C extends ICommandContext, A extends IAutocompleteContext> AnnotatedCommand<T, C, A> getFromMethod(@NotNull CommandManager<C, A> manager,
	                                                                                                                     @NotNull Class<T> clazz,
	                                                                                                                     @NotNull Method method,
	                                                                                                                     @NotNull Function<C, Optional<T>> instance,
	                                                                                                                     @NotNull Function<A, Optional<T>> autocompleteInstance) {
		Checks.notNull(manager, "manager");
		Checks.notNull(clazz, "clazz");
		Checks.notNull(method, "method");
		Checks.notNull(instance, "instance");
		Checks.notNull(autocompleteInstance, "autocompleteInstance");

		var info = method.getAnnotation(ApplicationCommand.class);
		if(info == null) throw new IllegalArgumentException("The provided method is not annotated with ApplicationCommand");

		return new AnnotatedCommand<>(manager, info, clazz, method, instance, autocompleteInstance);
	}

	@Override
	public void performCommand(@NotNull C context) throws Exception {
		if(method == null) return;

		if(info.defer()) context.getEvent().deferReply(true).queue();

		var instance = this.instance.apply(context);

		if(instance.isEmpty()) CommandManager.logger.warn("No instance found for '{}' with context {}", name, context);
		else {
			try {
				manager.getManager().invokeMethod(method, instance.get(), (i, p) -> {
					if(p.getType().isAssignableFrom(context.getEvent().getClass())) return context.getEvent();
					else if(p.getType().isAssignableFrom(context.getClass())) return context;
					else if(p.isAnnotationPresent(Option.class)) return manager.parseOption(context.getEvent(), getOptionName(p), p, p.getType(), method.getGenericParameterTypes()[i]);
					else return null;
				});
			} catch(CommandCancellation ignored) {

			} catch(InvocationTargetException e) {
				CommandManager.logger.error("Command threw exception", e.getCause());
				if(manager.exceptionHandler != null) manager.exceptionHandler.accept(context.getEvent(), new CommandException(this, e.getCause()));
			}
		}
	}

	private String getOptionName(Parameter param) {
		return param.getAnnotation(Option.class).name().isEmpty() ? param.getName() : param.getAnnotation(Option.class).name();
	}

	@SuppressWarnings("unchecked")
	private OptionData buildOption(Option info, Parameter param, Type generic, String name, Method autocomplete, Field choiceInfo) {
		OptionData option;

		if(autocomplete == null) option = new OptionData(manager.getOptionType(param.getType(), generic, param), name, "---", info.required());
		else option = new AutocompleteOption<A>(manager.getOptionType(param.getType(), generic, param), name, "---", info.required()) {
			@Override
			public void handleAutocomplete(@NotNull A context) {
				AnnotatedCommand.this.autocompleteInstance.apply(context).ifPresent(instance -> {
					try {
						manager.getManager().invokeMethod(autocomplete, instance, (i, p) -> {
							if(p.getType().isAssignableFrom(context.getEvent().getClass())) return context.getEvent();
							else if(p.getType().isAssignableFrom(context.getClass())) return context;
							else if(p.getType().isAssignableFrom(AnnotatedCommand.class)) return AnnotatedCommand.this;
							else return null;
						});
					} catch(InvocationTargetException e) {
						CommandManager.logger.error("autocomplete threw an exception", e.getCause());
					} catch(Exception e) {
						CommandManager.logger.error("Failed to perform autocomplete", e);
					}
				});
			}
		};

		var localization = manager.getManager().getLocalization(f -> f.getOptionPath(this, option), info.description());
		option.setDescription(localization.defaultValue()).setDescriptionLocalizations(localization.values());

		if(option.getType() == OptionType.STRING) {
			if(info.minLength() >= 0) option.setMinLength(info.minLength());
			if(info.maxLength() >= 0) option.setMaxLength(info.maxLength());
		} else if(option.getType() == OptionType.INTEGER) {
			if(info.minValue() != Double.MIN_VALUE) option.setMinValue((int) info.minValue());
			else if(info.maxValue() != Double.MIN_VALUE) option.setMaxValue((int) info.maxValue());
		} else if(option.getType() == OptionType.NUMBER) {
			if(info.minValue() != Double.MIN_VALUE) option.setMinValue(info.minValue());
			else if(info.maxValue() != Double.MIN_VALUE) option.setMaxValue(info.maxValue());
		} else if(option.getType() == OptionType.CHANNEL) option.setChannelTypes(info.channelTypes());

		if(choiceInfo != null) {
			this.instance.apply(null).ifPresent(instance -> {
				try {
					var choices = (Collection<? extends net.dv8tion.jda.api.interactions.commands.Command.Choice>) choiceInfo.get(instance);
					var prefix = choiceInfo.getAnnotation(Choice.class).prefix();

					option.addChoices(
							choices.stream()
									.peek(c -> {
										c.setName(prefix + c.getName());

										var cLocalization = manager.getManager().getLocalization(f -> f.getChoicePath(this, option, c), prefix.isEmpty() ? null : c.getName());
										c.setNameLocalizations(cLocalization.values());
									})
									.toList()
					);
				} catch(IllegalAccessException e) {
					CommandManager.logger.error("Failed to read choices", e);
				}
			});
		}

		return manager.configureOption(this, option, param, param.getType(), generic);
	}
}
