package de.mineking.discordutils.events.handlers;

import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ComponentHandler<T extends GenericComponentInteractionCreateEvent> extends FilteredEventHandler<T> {
	public final Consumer<T> handler;

	/**
	 * @param type    The java type of the event to handle
	 * @param filter  A filter
	 * @param handler The handler
	 */
	public ComponentHandler(@NotNull Class<T> type, @NotNull String filter, @NotNull Consumer<T> handler) {
		super(type, event -> filter.isEmpty() || event.getComponentId().matches(filter), false);

		Checks.notNull(handler, "handler");

		this.handler = handler;
	}

	@Override
	public void handleEvent(T event) {
		handler.accept(event);
	}
}
