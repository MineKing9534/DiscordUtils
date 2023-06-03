package de.mineking.discord.events;

import net.dv8tion.jda.api.events.GenericEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class EventWaiter<T extends GenericEvent> extends EventHandlerBase<T> {
	private final Predicate<T> filter;
	public final CompletableFuture<T> handler;

	public EventWaiter(EventManager manager, Class<T> type, Predicate<T> filter) {
		super(type);

		this.filter = filter;

		this.handler = new CompletableFuture<>();
		this.handler.whenComplete((x, e) -> manager.removeHandler(EventWaiter.this));
	}

	@Override
	public void handleEvent(T event) {
		if(filter.test(event)) {
			handler.complete(event);
		}
	}
}
