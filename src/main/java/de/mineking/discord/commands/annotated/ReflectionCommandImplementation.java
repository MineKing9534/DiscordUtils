package de.mineking.discord.commands.annotated;

import de.mineking.discord.commands.*;
import de.mineking.discord.commands.annotated.option.CustomOptionCreator;
import de.mineking.discord.commands.annotated.option.CustomOptionType;
import de.mineking.discord.commands.annotated.option.ExternalOption;
import de.mineking.discord.commands.annotated.option.Option;
import de.mineking.discord.commands.annotated.option.defaultValue.*;
import de.mineking.discord.commands.exception.CommandExecutionException;
import de.mineking.discord.commands.exception.ExecutionTermination;
import de.mineking.discord.localization.LocalizationManager;
import de.mineking.discord.localization.LocalizationPath;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class ReflectionCommandImplementation extends ReflectionCommandImplementationBase {
	public final CommandManager<?> manager;
	public final Method method;

	public ReflectionCommandImplementation(CommandManager<?> manager, CommandImplementation parent, Set<CommandImplementation> children, CommandInfo info, Class<?> type, Function<Object, Object> instance, Method method) {
		super(parent, children, info, type, instance);

		this.manager = manager;
		this.method = method;
	}

	@Override
	public void handle(GenericCommandInteractionEvent event) {
		try {
			var permission = getEffectivePermission();

			if(permission != null && !permission.isPermitted(manager, event)) {
				permission.handleUnpermitted(manager, event);
				return;
			}

			var params = new Object[method.getParameterCount()];
			var paramTypes = method.getParameters();

			var context = manager.getContext().createContext(manager, event);

			for(int i = 0; i < method.getParameterCount(); i++) {
				var param = paramTypes[i];

				if(param.getType().isAssignableFrom(context.getClass())) params[i] = context;
				else if(param.getType().isAssignableFrom(event.getClass())) params[i] = event;
				else if(param.isAnnotationPresent(Option.class) || param.isAnnotationPresent(ExternalOption.class)) {
					var value = getOption(event, getOptionNameFromParameter(manager, param), param.getType());
					params[i] = value == null ? getDefault(event, context, instance, param) : value;
				}
			}

			method.invoke(instance.apply(context), params);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch(ExecutionTermination ignored) {
		} catch(InvocationTargetException e) {
			throw new CommandExecutionException(this, e.getCause());
		}
	}

	@Override
	public void handleAutocomplete(CommandAutoCompleteInteractionEvent event) {
		var permission = getEffectivePermission();

		if(permission != null && !permission.isPermitted(manager, event)) return;

		for(var param : method.getParameters()) {
			if(Objects.equals(getOptionNameFromParameter(manager, param), event.getFocusedOption().getName())) {
				String resolver;
				Object object;

				var context = manager.getContext().createContext(manager, event);

				if(param.isAnnotationPresent(Option.class)) {
					resolver = param.getAnnotation(Option.class).autocomplete();
					object = instance.apply(context);
				} else {
					var type = param.getAnnotation(ExternalOption.class).value();

					resolver = type.getAnnotation(Option.class).autocomplete();
					object = manager.getExternalOption(type);
				}

				for(var m : object.getClass().getMethods()) {
					if(m.getName().equals(resolver)) {
						var params = new Object[m.getParameterCount()];

						for(int i = 0; i < m.getParameterCount(); i++) {
							var p = m.getParameters()[i];

							if(p.getType().isAssignableFrom(CommandAutoCompleteInteractionEvent.class)) params[i] = event;
							else if(p.getType().isAssignableFrom(manager.getContext().type)) params[i] = context;
						}

						try {
							m.invoke(object, params);
							return;
						} catch(IllegalAccessException e) {
							throw new RuntimeException(e);
						} catch(InvocationTargetException e) {
							throw new CommandExecutionException(this, e.getCause());
						}
					}
				}
			}
		}
	}

	public static String getOptionNameFromParameter(CommandManager<?> manager, Parameter param) {
		if(param.isAnnotationPresent(ExternalOption.class)) {
			var external = param.getAnnotation(ExternalOption.class);
			var type = external.value();
			var option = type.getAnnotation(Option.class);

			var instance = manager.getExternalOption(type);

			if(option == null || instance == null) throw new IllegalStateException("Invalid external option '" + type.getName() + "'");

			return !external.name().isEmpty()
					? external.name()
					: !option.name().isEmpty()
					? option.name()
					: param.getName().toLowerCase();
		} else if(param.isAnnotationPresent(Option.class)) {
			var option = param.getAnnotation(Option.class);

			return !option.name().isEmpty()
					? option.name()
					: param.getName().toLowerCase();
		}

		return null;
	}

	@Override
	public List<OptionData> getOptions(LocalizationManager localization) {
		return Stream.of(method.getParameters())
				.filter(param -> param.isAnnotationPresent(Option.class) || param.isAnnotationPresent(ExternalOption.class))
				.map(param -> {
					if(param.isAnnotationPresent(Option.class)) {
						var option = param.getAnnotation(Option.class);

						return getOptionFromAnnotation(option, param.getAnnotation(LocalizationPath.class), option.required(), param, type, instance.apply(null));
					} else {
						var external = param.getAnnotation(ExternalOption.class);
						var type = external.value();
						var option = type.getAnnotation(Option.class);

						var instance = manager.getExternalOption(type);

						if(option == null || instance == null) throw new IllegalStateException("Invalid external option '" + type.getName() + "'");

						return getOptionFromAnnotation(option, type.getAnnotation(LocalizationPath.class), external.required(), param, instance.getClass(), instance);
					}
				})
				.toList();
	}

	protected OptionType getOptionType(Class<?> type) {
		if(type.isAssignableFrom(String.class)) return OptionType.STRING;
		if(type.isEnum()) return OptionType.STRING;
		else if(type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) return OptionType.NUMBER;
		else if(type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class) || type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) return OptionType.INTEGER;
		else if(type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) return OptionType.BOOLEAN;
		else if(type.isAssignableFrom(User.class) || type.isAssignableFrom(Member.class)) return OptionType.USER;
		else if(type.isAssignableFrom(Role.class)) return OptionType.ROLE;
		else if(Channel.class.isAssignableFrom(type)) return OptionType.CHANNEL;
		else if(type.isAssignableFrom(IMentionable.class)) return OptionType.MENTIONABLE;
		else if(type.isAssignableFrom(Message.Attachment.class)) return OptionType.ATTACHMENT;
		else if(type.isAnnotationPresent(CustomOptionType.class)) return type.getAnnotation(CustomOptionType.class).type();

		return OptionType.UNKNOWN;
	}

	protected Object getDefault(GenericCommandInteractionEvent event, ContextBase context, Object instance, Parameter param) {
		if(param.isAnnotationPresent(EnumDefault.class)) return param.getType().getEnumConstants()[0];

		else if(param.isAnnotationPresent(BooleanDefault.class)) return param.getAnnotation(BooleanDefault.class).value();
		else if(param.isAnnotationPresent(IntegerDefault.class)) return param.getAnnotation(IntegerDefault.class).value();
		else if(param.isAnnotationPresent(DoubleDefault.class)) return param.getAnnotation(DoubleDefault.class).value();
		else if(param.isAnnotationPresent(StringDefault.class)) return param.getAnnotation(StringDefault.class).value();
		else if(param.isAnnotationPresent(DefaultFunction.class)) {
			for(var method : instance.getClass().getMethods()) {
				if(method.getName().equals(param.getAnnotation(DefaultFunction.class).value())) {
					var params = new Object[method.getParameterCount()];

					for(int i = 0; i < method.getParameterCount(); i++) {
						var p = method.getParameterTypes()[i];

						if(p.isAssignableFrom(context.getClass())) {
							params[i] = context;
						} else if(p.isAssignableFrom(event.getClass())) {
							params[i] = event;
						}
					}

					try {
						return method.invoke(instance, params);
					} catch(IllegalAccessException e) {
						throw new RuntimeException(e);
					} catch(InvocationTargetException e) {
						throw new RuntimeException("Failed to call option-default-value function", e.getCause());
					}
				}
			}
		}

		return null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected Object getOption(GenericCommandInteractionEvent event, String name, Class<?> type) {
		if(type.isAssignableFrom(String.class)) return event.getOption(name, OptionMapping::getAsString);
		if(type.isEnum()) return event.getOption(name, o -> Enum.valueOf((Class<? extends Enum>) type, o.getAsString()));
		else if(type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) return event.getOption(name, OptionMapping::getAsInt);
		else if(type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) return event.getOption(name, OptionMapping::getAsLong);
		else if(type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) return event.getOption(name, OptionMapping::getAsDouble);
		else if(type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) return event.getOption(name, OptionMapping::getAsBoolean);
		else if(type.isAssignableFrom(IMentionable.class)) return event.getOption(name, OptionMapping::getAsMentionable);
		else if(type.isAssignableFrom(Member.class)) return event.getOption(name, OptionMapping::getAsMember);
		else if(type.isAssignableFrom(User.class)) return event.getOption(name, OptionMapping::getAsUser);
		else if(type.isAssignableFrom(Role.class)) return event.getOption(name, OptionMapping::getAsRole);
		else if(Channel.class.isAssignableFrom(type)) return event.getOption(name, OptionMapping::getAsChannel);
		else if(type.isAssignableFrom(Message.Attachment.class)) return event.getOption(name, OptionMapping::getAsAttachment);
		else if(type.isAnnotationPresent(CustomOptionType.class)) {
			for(var m : type.getMethods()) {
				var creator = m.getAnnotation(CustomOptionCreator.class);

				if(creator != null) {
					try {
						return m.invoke(null, event, name);
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	protected OptionData getOptionFromAnnotation(Option paramInfo, LocalizationPath custom, boolean required, Parameter param, Class<?> instanceType, Object instance) {
		var name = getOptionNameFromParameter(manager, param);

		var localization = manager.getManager().getLocalization().getOptionDescription(getPath(), name, paramInfo, custom);

		var type = getOptionType(param.getType());

		if(type == OptionType.UNKNOWN) {
			throw new IllegalArgumentException("Option '" + name + "' with type '" + param.getType().getName() + "' could not be parsed");
		}

		var option = new OptionData(type, Objects.requireNonNull(name), localization.defaultValue, required, !paramInfo.autocomplete().isEmpty())
				.setDescriptionLocalizations(localization.values);

		if(option.getType() == OptionType.NUMBER) {
			if(paramInfo.minValue() > Double.MIN_VALUE) {
				option.setMinValue(paramInfo.minValue());
			}

			if(paramInfo.maxValue() > Double.MIN_VALUE) {
				option.setMaxValue(paramInfo.maxValue());
			}
		}

		if(option.getType() == OptionType.INTEGER) {
			if(paramInfo.minValue() > Double.MIN_VALUE) {
				option.setMinValue((long) paramInfo.minValue());
			}

			if(paramInfo.maxValue() > Double.MIN_VALUE) {
				option.setMaxValue((long) paramInfo.maxValue());
			}
		}

		if(option.getType() == OptionType.STRING) {
			if(paramInfo.minLength() > -1) option.setMinLength(paramInfo.minLength());
			if(paramInfo.maxLength() > -1) option.setMaxLength(paramInfo.maxLength());
		}

		if(paramInfo.channelTypes().length > 0) option.setChannelTypes(paramInfo.channelTypes());

		if(!paramInfo.choices().isEmpty() || param.getType().isEnum()) {
			try {
				var choices = paramInfo.choices().isEmpty()
						? new ArrayList<Choice>()
						: (Collection<Choice>) instanceType.getField(paramInfo.choices()).get(instance);

				if(param.getType().isEnum()) choices.addAll(
						Arrays.stream(param.getType().getEnumConstants())
								.map(x -> new Choice(x instanceof OptionEnum o ? o.getName() : x.toString(), x.toString()))
								.toList()
				);

				option.addChoices(choices.stream().map(c -> c.build(getPath(), name, manager.getManager().getLocalization())).toList());
			} catch(IllegalAccessException | ClassCastException e) {
				throw new RuntimeException(e);
			} catch(NoSuchFieldException e) {
				throw new IllegalArgumentException("Invalid choice field");
			}
		}

		return option;
	}
}
