package de.mineking.discordutils.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

public abstract class EventHandler<T extends GenericEvent> implements IEventHandler<T> {
	public final Class<T> type;
	public final boolean remove;

	/**
	 * @param type   The java type of the event to handle
	 * @param remove Whether to remove this handler after handling it
	 */
	public EventHandler(@NotNull Class<T> type, boolean remove) {
		Checks.notNull(type, "type");

		this.type = type;
		this.remove = remove;
	}

	/**
	 * Shortcut for {@link #EventHandler(Class, boolean)}, that does not remove the handler
	 */
	public EventHandler(@NotNull Class<T> type) {
		this(type, false);
	}

	@Override
	public boolean accepts(GenericEvent event) {
		return type.isAssignableFrom(event.getClass());
	}

	@Override
	public boolean handle(EventManager manager, T event) {
		handleEvent(event);
		return remove;
	}

	/**
	 * Event handler method
	 */
	public abstract void handleEvent(T event);
}
