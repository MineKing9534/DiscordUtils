package de.mineking.discordutils.events.handlers;

import de.mineking.discordutils.events.EventHandler;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public abstract class FilteredEventHandler<T extends GenericEvent> extends EventHandler<T> {
	public final Predicate<T> filter;

	/**
	 * @param type   The java type of the event to handle
	 * @param filter A filter
	 * @param remove Whether to remove the handler afterward
	 */
	public FilteredEventHandler(@NotNull Class<T> type, @NotNull Predicate<T> filter, boolean remove) {
		super(type, remove);

		Checks.notNull(filter, "filter");

		this.filter = filter;
	}

	/**
	 * Shortcut for {@link #FilteredEventHandler(Class, Predicate, boolean), that does not remove the handler
	 */
	public FilteredEventHandler(@NotNull Class<T> type, @NotNull Predicate<T> filter) {
		this(type, filter, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean accepts(GenericEvent event) {
		return super.accepts(event) && filter.test((T) event);
	}
}
