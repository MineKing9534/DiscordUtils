package de.mineking.discord.commands.annotated;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.localization.LocalizationManager;
import net.dv8tion.jda.api.interactions.commands.Command.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Creates an application command using annotations.
 * All nested classes of this class will be added as subcommands.
 *
 * @see CommandManager
 * @see CommandManager#registerCommand(Class)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationCommand {
	/**
	 * The name of the command
	 */
	String name();

	/**
	 * The description of the command. If the description is empty (default), the {@link LocalizationManager} is used to localize the description
	 */
	String description() default "";

	/**
	 * Whether the command should only be available on guilds. This only applies for global commands!
	 */
	boolean guildOnly() default false;

	/**
	 * The type of this application command. Default: {@link Type#SLASH}
	 */
	Type type() default Type.SLASH;

	/**
	 * The feature this command belongs to. If the feature is empty (default), this command will be interpreted as global command.
	 */
	String feature() default "";

	/**
	 * A list of additional subcommands
	 */
	Class<?>[] subcommands() default {};

	boolean defer() default false;
}
