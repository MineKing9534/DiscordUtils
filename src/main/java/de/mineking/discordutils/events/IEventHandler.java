package de.mineking.discordutils.events;

import net.dv8tion.jda.api.events.GenericEvent;

/**
 * @see EventHandler
 */
public interface IEventHandler<T extends GenericEvent> {
	/**
	 * @param event The event that was fired
	 * @return Whether this handler accepts the provided event
	 */
	boolean accepts(GenericEvent event);

	/**
	 * @param manager The responsible {@link EventManager}
	 * @param event   The event to handle
	 * @return Whether to remove the handler afterward
	 */
	boolean handle(EventManager manager, T event);
}
