package de.mineking.discordutils.commands;

import net.dv8tion.jda.api.interactions.commands.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the properties of an annotated command
 *
 * @see AnnotatedCommand
 * @see CommandManager#registerCommand(Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ApplicationCommand {
	/**
	 * The name of the command
	 */
	String name();

	/**
	 * The description of the command
	 */
	String description() default "";

	/**
	 * The {@link Command.Type} of this command
	 */
	Command.Type type() default Command.Type.SLASH;

	/**
	 * Whether to automatically defer this interaction
	 */
	boolean defer() default false;
}
