package de.mineking.discordutils.commands.condition.cooldown;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cooldown {
	/**
	 * The time interval in seconds to use for this cooldown
	 */
	int interval();

	/**
	 * The unit of {@link #interval()}
	 */
	TimeUnit unit() default TimeUnit.SECONDS;

	/**
	 * The number of allowed uses in the specified time interval.
	 * NOTE: THIS IS NOT SUPPORTED YET!
	 */
	int uses() default 1;

	/**
	 * An identifier of this cooldown. Can be used to use the same cooldown scope for multiple commands
	 *
	 * @see CooldownPool
	 */
	String identifier() default "";

	/**
	 * Whether to automatically increment the cooldown. Set this to false if you want to only increment the cooldown after validation in your handler method.
	 */
	boolean auto() default true;
}
