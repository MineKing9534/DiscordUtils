package de.mineking.discordutils.events;

import de.mineking.discordutils.Manager;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class EventManager extends Manager {
	public final static Logger logger = LoggerFactory.getLogger(EventManager.class);

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
	public void removeEventHandler(IEventHandler<?> handler) {
		eventHandlers.remove(handler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onGenericEvent(@NotNull GenericEvent event) {
		new HashSet<>(eventHandlers).removeIf(handler -> handler.accepts(event) && handler.handle(this, event));
	}
}
