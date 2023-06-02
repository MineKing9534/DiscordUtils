package de.mineking.discord.commands.annotated;

import net.dv8tion.jda.api.entities.Guild;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method that specified whether to add a guild command to a specific guild. It has to return a boolean and has to take a {@link Guild} as parameter.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuildCommandPredicate {
}
