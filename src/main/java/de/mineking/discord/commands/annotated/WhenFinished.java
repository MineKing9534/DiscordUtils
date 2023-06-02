package de.mineking.discord.commands.annotated;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.commands.CommandManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method that will be called after the command has successfully been registered. The method can have {@link CommandManager} and {@link DiscordUtils} parameters.
 *
 * @see CommandConstructor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WhenFinished {
}
