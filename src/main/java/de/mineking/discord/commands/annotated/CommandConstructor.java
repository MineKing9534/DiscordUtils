package de.mineking.discord.commands.annotated;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.commands.CommandManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A constructor of the command that will be used for instantiation. The constructor may have parameters of the type {@link CommandManager} or {@link DiscordUtils}.
 * Most of the time you should use {@link Setup} instead as it will be executed when the registration already finished. Only use {@link CommandConstructor} when you really have to!
 *
 * @apiNote If your class does not have a default constructor you have to annotate a constructor as {@link CommandConstructor}
 * @see Setup
 * @see ApplicationCommand
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandConstructor {
}
