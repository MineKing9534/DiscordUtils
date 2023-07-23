package de.mineking.discord.events;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class EventManager extends Module {
	private final Set<IEventHandler<?>> handlers = Collections.newSetFromMap(new ConcurrentHashMap<>());

	public EventManager(DiscordUtils manager) {
		super(manager);
	}

	@Override
	public void onGenericEvent(@NotNull GenericEvent event) {
		handlers.forEach(handler -> {
			if(handler.accepts(event)) {
				manager.getJDA().getCallbackPool().execute(() -> handler.accept(event));
			}
		});
	}

	public <T extends GenericEvent, U extends IEventHandler<T>> U registerHandler(U handler) {
		handlers.add(handler);

		return handler;
	}

	public <T extends GenericEvent> IEventHandler<T> removeHandler(IEventHandler<T> handler) {
		return handlers.remove(handler) ? handler : null;
	}

	public <T extends GenericEvent> CompletableFuture<T> waitForEvent(Class<T> type, Predicate<T> filter) {
		return registerHandler(new EventWaiter<>(this, type, filter)).handler;
	}

	public <T extends GenericEvent> CompletableFuture<T> waitForEvent(Class<T> type, Predicate<T> filter, Duration timeout) {
		return waitForEvent(type, filter).orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
	}

	public Set<IEventHandler<?>> getHandlers() {
		return Collections.unmodifiableSet(handlers);
	}
}
