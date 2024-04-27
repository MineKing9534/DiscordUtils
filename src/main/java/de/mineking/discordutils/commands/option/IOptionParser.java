package de.mineking.discordutils.commands.option;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.option.defaultValue.*;
import de.mineking.javautils.reflection.ReflectionUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @see CommandManager#registerOptionParser(IOptionParser)
 */
public interface IOptionParser {
	/**
	 * @param type  The java type of the parameter
	 * @param param The java method parameter. <b>Do not use {@link Parameter#getType()}! Use the 'type' parameter instead</b>
	 * @return Whether this option parser is responsible for the specified type
	 */
	boolean accepts(@NotNull Type type, @NotNull Parameter param);

	/**
	 * @param manager The responsible {@link CommandManager}
	 * @param type    The java type
	 * @param param   The java method parameter
	 * @return The {@link OptionType} that this option should use
	 * @see CommandManager#getOptionType(Type, Parameter)
	 */
	@NotNull
	OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param);

	/**
	 * Parses an option
	 *
	 * @param manager The responsible {@link CommandManager}
	 * @param event   The {@link GenericCommandInteractionEvent}
	 * @param name    The name of the option. This may be the same as the parameter name, but it is not required to!
	 * @param param   The java method parameter
	 * @param type    The java type of the parameter
	 * @return The resulting option
	 * @see CommandManager#parseOption(GenericCommandInteractionEvent, String, Parameter, Type)
	 */
	@Nullable
	Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type);

	/**
	 * Can be used to make final option configuration
	 *
	 * @param command The {@link de.mineking.discordutils.commands.Command}
	 * @param option  The current {@link OptionData}
	 * @param param   The java method parameter
	 * @param type    The java type of the parameter
	 * @return The resulting {@link OptionData}
	 */
	default OptionData configure(@NotNull de.mineking.discordutils.commands.Command<?> command, @NotNull OptionData option, @NotNull Parameter param, @NotNull Type type) {
		return option;
	}

	/**
	 * Can be used to configure the option registration
	 *
	 * @param cmd    The command to add the option to
	 * @param option The option
	 * @param param  the java method parameter
	 */
	default void registerOption(@NotNull de.mineking.discordutils.commands.Command<?> cmd, @NotNull OptionData option, @NotNull Parameter param) {
		cmd.addOption(option);
	}

	IOptionParser INTEGER = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return type.equals(int.class) || type.equals(Integer.class);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.INTEGER;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, () -> param.isAnnotationPresent(IntegerDefault.class) ? (int) param.getAnnotation(IntegerDefault.class).value() : null, OptionMapping::getAsInt);
		}
	};

	IOptionParser LONG = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return type.equals(long.class) || type.equals(Long.class);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.INTEGER;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, () -> param.isAnnotationPresent(IntegerDefault.class) ? param.getAnnotation(IntegerDefault.class).value() : null, OptionMapping::getAsLong);
		}
	};

	IOptionParser NUMBER = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return type.equals(double.class) || type.equals(Double.class);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.NUMBER;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, () -> param.isAnnotationPresent(DoubleDefault.class) ? param.getAnnotation(DoubleDefault.class).value() : null, OptionMapping::getAsDouble);
		}
	};

	IOptionParser BOOLEAN = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return type.equals(boolean.class) || type.equals(Boolean.class);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.BOOLEAN;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, () -> param.isAnnotationPresent(BooleanDefault.class) ? param.getAnnotation(BooleanDefault.class).value() : null, OptionMapping::getAsBoolean);
		}
	};

	IOptionParser ROLE = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return Role.class.isAssignableFrom(ReflectionUtils.getClass(type));
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.ROLE;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, OptionMapping::getAsRole);
		}
	};

	IOptionParser USER = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return User.class.isAssignableFrom(ReflectionUtils.getClass(type));
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.USER;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, OptionMapping::getAsUser);
		}
	};


	IOptionParser MEMBER = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return Member.class.isAssignableFrom(ReflectionUtils.getClass(type));
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.USER;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, OptionMapping::getAsMember);
		}
	};

	IOptionParser CHANNEL = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return Channel.class.isAssignableFrom(ReflectionUtils.getClass(type));
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.CHANNEL;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, OptionMapping::getAsChannel);
		}
	};

	IOptionParser MENTIONABLE = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return IMentionable.class.isAssignableFrom(ReflectionUtils.getClass(type));
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.MENTIONABLE;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, OptionMapping::getAsMentionable);
		}
	};

	IOptionParser ATTACHMENT = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return Message.Attachment.class.isAssignableFrom(ReflectionUtils.getClass(type));
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.ATTACHMENT;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, OptionMapping::getAsAttachment);
		}
	};

	IOptionParser STRING = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return ReflectionUtils.getClass(type).isAssignableFrom(String.class);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.STRING;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, () -> param.isAnnotationPresent(StringDefault.class) ? param.getAnnotation(StringDefault.class).value() : null, OptionMapping::getAsString);
		}
	};

	IOptionParser OPTIONAL = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return type.equals(Optional.class);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return manager.getOptionType(ReflectionUtils.getComponentType(type), param);
		}

		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return Optional.ofNullable(manager.parseOption(event, name, param, ReflectionUtils.getComponentType(type)));
		}
	};

	IOptionParser ENUM = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return ReflectionUtils.getClass(type).isEnum();
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return OptionType.STRING;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			return event.getOption(name, o -> ReflectionUtils.getEnumConstant(type, o.getAsString()).orElseGet(() -> {
				var def = param.getAnnotation(EnumDefault.class);
				if(def == null) return null;

				return def.value().isEmpty() ? (Enum<?>) ReflectionUtils.getClass(type).getEnumConstants()[0] : ReflectionUtils.getEnumConstant(type, def.value()).orElse(null);
			}));
		}

		@Override
		public OptionData configure(@NotNull de.mineking.discordutils.commands.Command<?> command, @NotNull OptionData option, @NotNull Parameter param, @NotNull Type type) {
			var clazz = ReflectionUtils.getClass(type);

			return option.addChoices(Arrays.stream((Enum<?>[]) clazz.getEnumConstants()).filter(e -> {
				try {
					return !clazz.getField(e.name()).isAnnotationPresent(IgnoreEnumConstant.class);
				} catch(NoSuchFieldException ex) {
					throw new RuntimeException(ex);
				}
			}).map(c -> new Command.Choice(c.toString(), c.name())).peek(c -> {
				var localization = command.getManager().getManager().getLocalization(f -> f.getChoicePath(command, option, c), null);
				c.setNameLocalizations(localization.values());
			}).toList());
		}
	};

	IOptionParser ARRAY = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Type type, @NotNull Parameter param) {
			return ReflectionUtils.isArray(type, true);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Type type, @NotNull Parameter param) {
			return manager.getOptionType(ReflectionUtils.getComponentType(type), param);
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Type type) {
			var component = ReflectionUtils.getComponentType(type);

			var array = event.getOptions().stream().filter(o -> o.getName().matches(Matcher.quoteReplacement(name) + "\\d+")).map(o -> manager.parseOption(event, o.getName(), param, component)).toList();

			return ReflectionUtils.isArray(type, false) ? array.toArray(i -> ReflectionUtils.createArray(component, i)) : createCollection(ReflectionUtils.getClass(type), ReflectionUtils.getClass(component), array);
		}


		@SuppressWarnings("unchecked")
		private <C> Collection<C> createCollection(Class<?> type, Class<?> component, List<C> array) {
			if(type.isAssignableFrom(List.class)) return new ArrayList<>(array);
			else if(type.isAssignableFrom(Set.class)) return new HashSet<>(array);
			else if(type.isAssignableFrom(EnumSet.class)) return (Collection<C>) createEnumSet(array, component);

			throw new IllegalStateException("Cannot create collection for " + type.getTypeName() + " with component " + component.getTypeName());
		}

		@SuppressWarnings("unchecked")
		private <E extends Enum<E>> EnumSet<E> createEnumSet(Collection<?> collection, Class<?> component) {
			return collection.isEmpty() ? EnumSet.noneOf((Class<E>) component) : EnumSet.copyOf((Collection<E>) collection);
		}

		@Override
		public void registerOption(@NotNull de.mineking.discordutils.commands.Command<?> cmd, @NotNull OptionData option, @NotNull Parameter param) {
			var oa = param.getAnnotation(OptionArray.class);

			if(oa == null) IOptionParser.super.registerOption(cmd, option, param);
			else {
				for(int i = 1; i <= oa.maxCount(); i++) {
					var o = OptionData.fromData(option.toData()).setName(option.getName() + i);

					if(i <= oa.minCount()) IOptionParser.super.registerOption(cmd, o, param);
					else IOptionParser.super.registerOption(cmd, o.setRequired(false), param);
				}
			}
		}
	};
}
