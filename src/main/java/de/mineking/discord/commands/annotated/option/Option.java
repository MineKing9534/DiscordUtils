package de.mineking.discord.commands.annotated.option;

import net.dv8tion.jda.api.entities.channel.ChannelType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
	String name() default "";

	String description() default "";

	boolean required() default true;

	double minValue() default Double.MIN_VALUE;

	double maxValue() default Double.MIN_VALUE;

	int minLength() default -1;

	int maxLength() default -1;

	ChannelType[] channelTypes() default {};

	String choices() default "";

	String autocomplete() default "";
}
