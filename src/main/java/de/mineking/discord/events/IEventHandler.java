package de.mineking.discord.events;

import net.dv8tion.jda.api.events.GenericEvent;

public interface IEventHandler<T extends GenericEvent> {
	boolean accepts(GenericEvent event);

	@SuppressWarnings("unchecked")
	default void accept(GenericEvent event) {
		handleEvent((T) event);
	}

	void handleEvent(T event);
}
