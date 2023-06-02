package de.mineking.discord.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

public abstract class EventHandlerBase<T extends GenericEvent> implements IEventHandler<T> {
	protected final Class<T> type;

	public EventHandlerBase(@NotNull Class<T> type) {
		Checks.notNull(type, "type");

		this.type = type;
	}

	@Override
	public boolean accepts(GenericEvent event) {
		return type.isInstance(event);
	}
}
