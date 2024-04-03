package de.mineking.discordutils.events;

import de.mineking.discordutils.Manager;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

	public void registerListener(@NotNull Class<?> type, @NotNull Supplier<Object> instance) {
		for(var m : type.getMethods()) {
			var listener = m.getAnnotation(Listener.class);
			if(listener == null) continue;

			try {
				addEventHandler(getManager().createInstance(listener.type(), (i, p) -> {
					if(p.getName().equals("filter")) return listener.filter();
					else if(p.getName().equals("handler")) return (Consumer<?>) event -> {
						try {
							getManager().invokeMethod(m, instance.get(), (x, mp) -> {
								if(mp.getType().isAssignableFrom(event.getClass())) return event;
								else return null;
							});
						} catch(InvocationTargetException e) {
							logger.error("An error occurred in listener method", e.getCause());
						} catch(Exception e) {
							logger.error("Failed to invoke listener method", e);
						}
					};
					else return null;
				}));
			} catch(Exception e) {
				logger.error("Failed to instantiate event handler for listener method");
			}
		}
	}

	public void registerListeners(@NotNull Object... objects) {
		for(var o : objects) {
			registerListener(o.getClass(), () -> o);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onGenericEvent(@NotNull GenericEvent event) {
		new HashSet<>(eventHandlers).removeIf(handler -> handler.accepts(event) && handler.handle(this, event));
	}
}
