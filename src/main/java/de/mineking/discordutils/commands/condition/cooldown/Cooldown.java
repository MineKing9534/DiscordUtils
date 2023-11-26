package de.mineking.discordutils.commands.condition.cooldown;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cooldown {
	/**
	 * The time interval in seconds to use for this cooldown
	 */
	int interval();

	/**
	 * The number of allowed uses in the specified time interval.
	 * @apiNote THIS IS NOT SUPPORTED YET!
	 */
	int uses() default 1;
}
