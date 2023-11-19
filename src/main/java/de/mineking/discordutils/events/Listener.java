package de.mineking.discordutils.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {
	/**
	 * The class of the handler that is responsible for calling the method. By default, you can use {@link de.mineking.discordutils.events.handlers.ButtonHandler}, {@link de.mineking.discordutils.events.handlers.StringSelectHandler},
	 * {@link de.mineking.discordutils.events.handlers.EntitySelectHandler} and {@link de.mineking.discordutils.events.handlers.ModalHandler}.
	 */
	Class<? extends IEventHandler<?>> type();

	/**
	 * A string that filters which events to handle
	 */
	String filter() default "";
}
