package de.mineking.discordutils.commands.option;

import net.dv8tion.jda.api.entities.channel.ChannelType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Option {
	/**
	 * The name of the option. If this is not set, the parameter name will be used
	 */
	String name() default "";

	/**
	 * An id for this option that will be used for autocomplete and choice identification. In most cases, you can keep this blank so that the option's effective name is used
	 */
	String id() default "";

	/**
	 * The description of the option
	 */
	String description() default "";

	/**
	 * Whether this option is required. When this is set to {@code false}, you may not use primitive types! Use either the class wrappers or {@link java.util.Optional}s in that case.
	 */
	boolean required() default true;

	/**
	 * The minimum allowed value. Only for {@link net.dv8tion.jda.api.interactions.commands.OptionType#NUMBER} and {@link net.dv8tion.jda.api.interactions.commands.OptionType#INTEGER}
	 */
	double minValue() default Double.MIN_VALUE;

	/**
	 * The maximum allowed value. Only for {@link net.dv8tion.jda.api.interactions.commands.OptionType#NUMBER} and {@link net.dv8tion.jda.api.interactions.commands.OptionType#INTEGER}
	 */
	double maxValue() default Double.MIN_VALUE;

	/**
	 * The minimum required length for the input. Only for {@link net.dv8tion.jda.api.interactions.commands.OptionType#STRING}
	 */
	int minLength() default -1;

	/**
	 * The maximum required length for the input. Only for {@link net.dv8tion.jda.api.interactions.commands.OptionType#STRING}
	 */
	int maxLength() default -1;

	/**
	 * The supportet channel types. Only for {@link net.dv8tion.jda.api.interactions.commands.OptionType#CHANNEL}
	 */
	ChannelType[] channelTypes() default {};
}
