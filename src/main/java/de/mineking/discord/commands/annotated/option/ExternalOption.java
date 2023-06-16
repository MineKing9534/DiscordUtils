package de.mineking.discord.commands.annotated.option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method parameter as option while referring to an external class that holds the options properties. This is useful when you use the same option in multiple commands.
 *
 * @apiNote The target option class has to be annotated with {@link Option}
 * @see Option
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalOption {
	/**
	 * The class of the external option
	 */
	Class<?> value();

	/**
	 * The name of the command. If the name is empty (default) the name of the parameter is used.
	 */
	String name() default "";

	/**
	 * Whether users have to specify a value for this option. If you use primitive types as option types make sure you use the class wrappers if the option is optional! (primitives cannot be null)
	 */
	boolean required() default true;
}
