package de.mineking.discordutils.commands.option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Choice {
	/**
	 * The name of the option
	 */
	String value();

	/**
	 * A prefix that is added to all choices' names. If localization is enabled, you can change the localization path with this
	 */
	String prefix() default "";
}
