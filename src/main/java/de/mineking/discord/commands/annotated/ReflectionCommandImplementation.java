package de.mineking.discord.commands.annotated;

import de.mineking.discord.commands.Choice;
import de.mineking.discord.commands.CommandImplementation;
import de.mineking.discord.commands.CommandInfo;
import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.exception.CommandExecutionException;
import de.mineking.discord.localization.LocalizationManager;
import de.mineking.discord.localization.LocalizationPath;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class ReflectionCommandImplementation extends ReflectionCommandImplementationBase {
	public final CommandManager<?> manager;
	public final Method method;

	public ReflectionCommandImplementation(CommandManager<?> manager, CommandImplementation parent, Set<CommandImplementation> children, CommandInfo info, Object instance, Method method) {
		super(parent, children, info, instance);

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

				if(param.getType().isAssignableFrom(context.getClass())) {
					params[i] = context;
				}

				else if(param.getType().isAssignableFrom(getEventType(info.type))) {
					params[i] = event;
				}

				else if(param.isAnnotationPresent(Option.class) || param.isAnnotationPresent(ExternalOption.class)) {
					params[i] = getOption(event, getOptionNameFromParameter(param), param.getType());
				}
			}

			method.invoke(instance, params);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch(InvocationTargetException e) {
			throw new CommandExecutionException(this, e.getCause());
		}
	}

	@Override
	public void handleAutocomplete(CommandAutoCompleteInteractionEvent event) {
		var permission = getEffectivePermission();

		if(permission != null && !permission.isPermitted(manager, event)) {
			return;
		}

		for(var param : method.getParameters()) {
			if(Objects.equals(getOptionNameFromParameter(param), event.getFocusedOption().getName())) {
				String resolver;
				Object object;

				if(param.isAnnotationPresent(Option.class)) {
					resolver = param.getAnnotation(Option.class).autocomplete();
					object = instance;
				}

				else {
					var type = param.getAnnotation(ExternalOption.class).value();

					resolver = type.getAnnotation(Option.class).autocomplete();
					object = manager.getExternalOption(type);
				}

				var context = manager.getContext().createContext(manager, event);

				for(var m : object.getClass().getMethods()) {
					if(m.getName().equals(resolver)) {
						var params = new Object[m.getParameterCount()];

						for(int i = 0; i < m.getParameterCount(); i++) {
							var p = m.getParameters()[i];

							if(p.getType().isAssignableFrom(CommandAutoCompleteInteractionEvent.class)) {
								params[i] = event;
							}

							else if(p.getType().isAssignableFrom(manager.getContext().type)) {
								params[i] = context;
							}
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

	private String getOptionNameFromParameter(Parameter param) {
		if(param.isAnnotationPresent(ExternalOption.class)) {
			var external = param.getAnnotation(ExternalOption.class);
			var type = external.value();
			var option = type.getAnnotation(Option.class);

			var instance = manager.getExternalOption(type);

			if(option == null || instance == null) {
				throw new IllegalStateException("Invalid external option '" + type.getName() + "'");
			}

			return !external.name().isEmpty()
					? external.name()
					: !option.name().isEmpty()
					? option.name()
					: param.getName().toLowerCase();
		}

		else if(param.isAnnotationPresent(Option.class)) {
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

						return getOptionFromAnnotation(option, param.getAnnotation(LocalizationPath.class), option.required(), param, instance);
					}

					else {
						var external = param.getAnnotation(ExternalOption.class);
						var type = external.value();
						var option = type.getAnnotation(Option.class);

						var instance = manager.getExternalOption(type);

						if(option == null || instance == null) {
							throw new IllegalStateException("Invalid external option '" + type.getName() + "'");
						}

						return getOptionFromAnnotation(option, type.getAnnotation(LocalizationPath.class), external.required(), param, instance);
					}
				})
				.toList();
	}

	protected OptionType getOptionType(Class<?> type) {
		if(type.isAssignableFrom(String.class)) {
			return OptionType.STRING;
		}

		else if(type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
			return OptionType.NUMBER;
		}

		else if(type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class) || type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
			return OptionType.INTEGER;
		}

		else if(type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
			return OptionType.BOOLEAN;
		}

		else if(type.isAssignableFrom(User.class)) {
			return OptionType.USER;
		}

		else if(type.isAssignableFrom(Role.class)) {
			return OptionType.ROLE;
		}

		else if(type.isAssignableFrom(Channel.class)) {
			return OptionType.CHANNEL;
		}

		else if(type.isAssignableFrom(IMentionable.class)) {
			return OptionType.MENTIONABLE;
		}

		else if(type.isAssignableFrom(Message.Attachment.class)) {
			return OptionType.ATTACHMENT;
		}

		return OptionType.UNKNOWN;
	}

	protected Class<? extends GenericCommandInteractionEvent> getEventType(Command.Type type) {
		return switch(type) {
			case SLASH -> SlashCommandInteractionEvent.class;
			case MESSAGE -> MessageContextInteractionEvent.class;
			case USER -> UserContextInteractionEvent.class;
			case UNKNOWN -> null;
		};
	}

	protected Object getOption(GenericCommandInteractionEvent event, String name, Class<?> type) {
		if(type.isAssignableFrom(String.class)) {
			return event.getOption(name, OptionMapping::getAsString);
		}

		else if(type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
			return event.getOption(name, OptionMapping::getAsInt);
		}

		else if(type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
			return event.getOption(name, OptionMapping::getAsLong);
		}

		else if(type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
			return event.getOption(name, OptionMapping::getAsDouble);
		}

		else if(type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
			return event.getOption(name, OptionMapping::getAsBoolean);
		}

		else if(type.isAssignableFrom(IMentionable.class)) {
			return event.getOption(name, OptionMapping::getAsMentionable);
		}

		else if(type.isAssignableFrom(Member.class)) {
			return event.getOption(name, OptionMapping::getAsMember);
		}

		else if(type.isAssignableFrom(User.class)) {
			return event.getOption(name, OptionMapping::getAsUser);
		}

		else if(type.isAssignableFrom(Role.class)) {
			return event.getOption(name, OptionMapping::getAsRole);
		}

		else if(type.isAssignableFrom(Channel.class)) {
			return event.getOption(name, OptionMapping::getAsChannel);
		}

		else if(type.isAssignableFrom(Message.Attachment.class)) {
			return event.getOption(name, OptionMapping::getAsAttachment);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	protected OptionData getOptionFromAnnotation(Option paramInfo, LocalizationPath custom, boolean required, Parameter param, Object instance) {
		var name = getOptionNameFromParameter(param);

		var localization = manager.getManager().getLocalization().getOptionDescription(getLocalizationPath(), name, paramInfo, custom);

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
			if(paramInfo.minLength() > -1) {
				option.setMinLength(paramInfo.minLength());
			}

			if(paramInfo.maxLength() > -1) {
				option.setMaxLength(paramInfo.maxLength());
			}
		}

		if(paramInfo.channelTypes().length > 0) {
			option.setChannelTypes(paramInfo.channelTypes());
		}

		if(!paramInfo.choices().isEmpty()) {
			try {
				var choices = (Collection<? extends Choice>) instance.getClass().getField(paramInfo.choices()).get(instance);

				option.addChoices(choices.stream().map(c -> c.build(getLocalizationPath(), name, manager.getManager().getLocalization())).toList());
			} catch(IllegalAccessException | ClassCastException e) {
				throw new RuntimeException(e);
			} catch(NoSuchFieldException e) {
				throw new IllegalArgumentException("Invalid choice field");
			}
		}

		return option;
	}
}
