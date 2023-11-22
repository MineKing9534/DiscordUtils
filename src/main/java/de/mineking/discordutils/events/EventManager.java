package de.mineking.discordutils.events;

import de.mineking.discordutils.Manager;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class EventManager extends Manager {
	@SuppressWarnings("rawtypes")
	private final Set<IEventHandler> eventHandlers = new HashSet<>();

	/**
	 * @param handler The {@link IEventHandler} to add
	 */
	public void addEventHandler(IEventHandler<?> handler) {
		eventHandlers.add(handler);
	}

	/**
	 * @param handler The {@link IEventHandler} to remove
	 */
	public void removeEventHAndler(IEventHandler<?> handler) {
		eventHandlers.remove(handler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onGenericEvent(@NotNull GenericEvent event) {
		eventHandlers.removeIf(handler -> handler.accepts(event) && handler.handle(this, event));
	}
}
