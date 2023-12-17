package de.mineking.discordutils.commands.condition.cooldown;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CooldownPool {
	/**
	 * The identifier of the cooldown
	 */
	String value();
}
