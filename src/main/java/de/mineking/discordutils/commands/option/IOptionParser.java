package de.mineking.discordutils.commands.option;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.option.defaultValue.*;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * @see CommandManager#registerOptionParser(IOptionParser)
 */
public interface IOptionParser {
	/**
	 * @param type The java type of the parameter
	 * @return Whether this option parser is responsible for the specified type
	 */
	boolean accepts(@NotNull Class<?> type);

	/**
	 * @param manager The responsible {@link CommandManager}
	 * @param type    The java type
	 * @param generic The generic type information
	 * @return The {@link OptionType} that this option should use
	 * @see CommandManager#getOptionType(Class, Type)
	 */
	@NotNull
	OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic);

	/**
	 * Parses an option
	 *
	 * @param manager The responsible {@link CommandManager}
	 * @param event   The {@link GenericCommandInteractionEvent}
	 * @param name    The name of the option. This may be the same as the parameter name, but it is not required to!
	 * @param param   The java method parameter
	 * @param type    The java type of the parameter
	 * @param generic The parameter's generic type information
	 * @return The resulting option
	 * @see CommandManager#parseOption(GenericCommandInteractionEvent, String, Parameter, Class, Type)
	 */
	@Nullable
	Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic);

	/**
	 * Can be used to make final option configuration
	 *
	 * @param command The {@link de.mineking.discordutils.commands.Command}
	 * @param option  The current {@link OptionData}
	 * @param param   The java method parameter
	 * @param type    The java type of the parameter
	 * @param generic The generic type information
	 */
	default void configure(@NotNull de.mineking.discordutils.commands.Command<?> command, @NotNull OptionData option, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {}

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
		public boolean accepts(@NotNull Class<?> type) {
			return int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.INTEGER;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, () -> param.isAnnotationPresent(IntegerDefault.class) ? (int) param.getAnnotation(IntegerDefault.class).value() : null, OptionMapping::getAsInt);
		}
	};

	IOptionParser LONG = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.INTEGER;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, () -> param.isAnnotationPresent(IntegerDefault.class) ? param.getAnnotation(IntegerDefault.class).value() : null, OptionMapping::getAsLong);
		}
	};

	IOptionParser NUMBER = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.NUMBER;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, () -> param.isAnnotationPresent(DoubleDefault.class) ? param.getAnnotation(DoubleDefault.class).value() : null, OptionMapping::getAsDouble);
		}
	};

	IOptionParser BOOLEAN = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.BOOLEAN;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, () -> param.isAnnotationPresent(BooleanDefault.class) ? param.getAnnotation(BooleanDefault.class).value() : null, OptionMapping::getAsBoolean);
		}
	};

	IOptionParser ROLE = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return Role.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.ROLE;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, OptionMapping::getAsRole);
		}
	};

	IOptionParser USER = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return User.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.USER;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, OptionMapping::getAsUser);
		}
	};

	IOptionParser CHANNEL = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return Channel.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.CHANNEL;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, OptionMapping::getAsChannel);
		}
	};

	IOptionParser MENTIONABLE = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return IMentionable.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.MENTIONABLE;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, OptionMapping::getAsMentionable);
		}
	};

	IOptionParser ATTACHMENT = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return Message.Attachment.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.ATTACHMENT;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, OptionMapping::getAsAttachment);
		}
	};

	IOptionParser STRING = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return String.class.isAssignableFrom(type);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.STRING;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return event.getOption(name, () -> param.isAnnotationPresent(StringDefault.class) ? param.getAnnotation(StringDefault.class).value() : null, OptionMapping::getAsString);
		}
	};

	IOptionParser OPTIONAL = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return type.equals(Optional.class);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			var p = ((ParameterizedType) generic).getActualTypeArguments()[0];
			return manager.getOptionType((Class<?>) p, p);
		}

		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			var p = ((ParameterizedType) generic).getActualTypeArguments()[0];
			return Optional.ofNullable(manager.parseOption(event, name, param, (Class<?>) p, p));
		}
	};

	IOptionParser ENUM = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return type.isEnum();
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			return OptionType.STRING;
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			return getEnumConstant(type, event.getOption(name, OptionMapping::getAsString)).orElseGet(() -> {
				var def = param.getAnnotation(EnumDefault.class);
				if(def == null) return null;

				return def.value().isEmpty() ? (Enum<?>) type.getEnumConstants()[0] : getEnumConstant(type, def.value()).orElse(null);
			});
		}

		private Optional<Enum<?>> getEnumConstant(Class<?> type, String name) {
			return Arrays.stream((Enum<?>[]) type.getEnumConstants())
					.filter(c -> c.name().equals(name))
					.findFirst();
		}

		@Override
		public void configure(@NotNull de.mineking.discordutils.commands.Command<?> command, @NotNull OptionData option, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			var choices = Arrays.stream((Enum<?>[]) type.getEnumConstants())
					.map(c -> new Command.Choice(c.toString(), c.name()))
					.toList();

			option.addChoices(
					choices.stream()
							.peek(c -> {
								var localization = command.manager.manager.getLocalization(f -> f.getChoicePath(command, option, c), null);
								c.setNameLocalizations(localization.values());
							})
							.toList()
			);
		}
	};

	IOptionParser ARRAY = new IOptionParser() {
		@Override
		public boolean accepts(@NotNull Class<?> type) {
			return type.isArray() || type.isAssignableFrom(List.class);
		}

		@NotNull
		@Override
		public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
			if(type.isArray()) return manager.getOptionType(type.getComponentType(), generic);
			else {
				var p = ((ParameterizedType) generic).getActualTypeArguments()[0];
				return manager.getOptionType((Class<?>) p, p);
			}
		}

		@Nullable
		@Override
		public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
			var result = new ArrayList<>();

			Class<?> ct;
			Type cg;

			if(type.isArray()) {
				ct = type.getComponentType();
				cg = generic;
			} else {
				var p = ((ParameterizedType) generic).getActualTypeArguments()[0];
				ct = (Class<?>) p;
				cg = p;
			}

			for(var o : event.getOptions()) {
				if(o.getName().matches(Matcher.quoteReplacement(name) + "\\d+")) {
					result.add(manager.parseOption(event, o.getName(), param, ct, cg));
				}
			}

			return type.isArray() ? result.toArray(l -> (Object[]) Array.newInstance(type.getComponentType(), l)) : result;
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
