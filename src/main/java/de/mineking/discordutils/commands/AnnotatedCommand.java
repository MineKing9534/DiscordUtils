package de.mineking.discordutils.commands;

import de.mineking.discordutils.commands.condition.execution.IExecutionCondition;
import de.mineking.discordutils.commands.condition.registration.IRegistrationCondition;
import de.mineking.discordutils.commands.context.ContextBase;
import de.mineking.discordutils.commands.option.Autocomplete;
import de.mineking.discordutils.commands.option.AutocompleteOption;
import de.mineking.discordutils.commands.option.Choice;
import de.mineking.discordutils.commands.option.Option;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
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
public class AnnotatedCommand<T, C extends ContextBase<? extends GenericCommandInteractionEvent>, A extends ContextBase<CommandAutoCompleteInteractionEvent>> extends Command<C> {
	@NotNull
	public final Class<T> clazz;
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

		this.instance = instance;
		this.autocompleteInstance = autocompleteInstance;

		for(var m : clazz.getMethods()) {
			if(!m.isAnnotationPresent(Setup.class)) continue;

			try {
				manager.manager.invokeMethod(m, null, p -> {
					if(p.getType().isAssignableFrom(AnnotatedCommand.class)) return this;
					else if(p.getType().isAssignableFrom(CommandManager.class)) return manager;
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
					else if(IExecutionCondition.class.isAssignableFrom(f.getType())) condition = (IExecutionCondition<C>) f.get(null);
					else if(IRegistrationCondition.class.isAssignableFrom(f.getType())) registration = (IRegistrationCondition<C>) f.get(null);
				} catch(Exception e) {
					CommandManager.logger.error("Failed to read field", e);
				}
			}

			for(int i = 0; i < method.getParameterCount(); i++) {
				var p = method.getParameters()[i];
				var generic = method.getGenericParameterTypes()[i];

				var o = p.getAnnotation(Option.class);
				if(o == null) continue;

				var name = getOptionName(p);

				manager.getParser(p.getType()).ifPresent(op -> op.registerOption(this, buildOption(o, p, generic, name, autocomplete.get(o.id().isEmpty() ? name : o.id()), choices.get(o.id().isEmpty() ? name : o.id())), p));
			}
		}

		for(var t : clazz.getClasses()) {
			if(!t.isAnnotationPresent(ApplicationCommand.class)) continue;

			var i = t.getAnnotation(ApplicationCommand.class);
			if(i != null) addSubcommand(createCommand(t, i, manager));
		}

		if(getSubcommands().isEmpty() && method == null) CommandManager.logger.warn("Command '{}' has neither subcommands nor a method", name);
	}

	private static <T, C extends ContextBase<? extends GenericCommandInteractionEvent>, A extends ContextBase<CommandAutoCompleteInteractionEvent>> AnnotatedCommand<T, C, A> createCommand(Class<T> type, ApplicationCommand info,
	                                                                                                                                                                                        CommandManager<C, A> manager) {
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
	public static <T, C extends ContextBase<? extends GenericCommandInteractionEvent>, A extends ContextBase<CommandAutoCompleteInteractionEvent>> AnnotatedCommand<T, C, A> getFromClass(@NotNull CommandManager<C, A> manager,
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

	public static <T, C extends ContextBase<? extends GenericCommandInteractionEvent>, A extends ContextBase<CommandAutoCompleteInteractionEvent>> AnnotatedCommand<T, C, A> getFromMethod(@NotNull CommandManager<C, A> manager,
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

		var instance = this.instance.apply(context);

		if(instance.isEmpty()) CommandManager.logger.warn("No instance found for '{}' with context {}", name, context);
		else {
			try {
				method.invoke(instance.get(), buildParameters(context));
			} catch(InvocationTargetException e) {
				CommandManager.logger.error("Command threw exception", e.getCause());
				if(manager.exceptionHandler != null) manager.exceptionHandler.accept(context.event, new CommandException(this, e.getCause()));
			}
		}
	}

	private String getOptionName(Parameter param) {
		return param.getAnnotation(Option.class).name().isEmpty() ? param.getName() : param.getAnnotation(Option.class).name();
	}

	@SuppressWarnings("unchecked")
	private OptionData buildOption(Option info, Parameter param, Type generic, String name, Method autocomplete, Field choiceInfo) {
		OptionData option;

		if(autocomplete == null) option = new OptionData(manager.getOptionType(param.getType(), generic), name, "---", info.required());
		else option = new AutocompleteOption<A>(manager.getOptionType(param.getType(), generic), name, "---", info.required()) {
			@Override
			public void handleAutocomplete(@NotNull A context) {
				AnnotatedCommand.this.autocompleteInstance.apply(context).ifPresent(instance -> {
					try {
						manager.manager.invokeMethod(autocomplete, instance, p -> {
							if(p.getType().isAssignableFrom(context.event.getClass())) return context.event;
							else if(p.getType().isAssignableFrom(CommandManager.class)) return manager;
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

		var localization = manager.manager.getLocalization(f -> f.getOptionPath(this, option), info.description());
		option.setDescription(localization.defaultValue()).setDescriptionLocalizations(localization.values());

		if(option.getType() == OptionType.STRING) {
			if(info.minLength() >= 0) option.setMinLength(info.minLength());
			if(info.maxLength() >= 0) option.setMaxLength(info.maxLength());
		} else if(option.getType() == OptionType.INTEGER || option.getType() == OptionType.NUMBER) {
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

										var cLocalization = manager.manager.getLocalization(f -> f.getChoicePath(this, option, c), prefix.isEmpty() ? null : c.getName());
										c.setNameLocalizations(cLocalization.values());
									})
									.toList()
					);
				} catch(IllegalAccessException e) {
					CommandManager.logger.error("Failed to read choices", e);
				}
			});
		}

		manager.configureOption(this, option, param, param.getType(), generic);

		return option;
	}

	private Object[] buildParameters(C context) {
		var parameters = new Object[method.getParameterCount()];

		for(int i = 0; i < method.getParameterCount(); i++) {
			var p = method.getParameters()[i];

			if(p.getType().isAssignableFrom(context.event.getClass())) parameters[i] = context.event;
			else if(p.getType().isAssignableFrom(context.getClass())) parameters[i] = context;
			else if(p.isAnnotationPresent(Option.class)) parameters[i] = manager.parseOption(context.event, getOptionName(p), p, p.getType(), method.getGenericParameterTypes()[i]);
		}

		return parameters;
	}
}
