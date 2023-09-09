package de.mineking.discord.ui.components;

import de.mineking.discord.ui.Menu;
import de.mineking.discord.ui.MenuBase;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public abstract class Component<T extends GenericComponentInteractionCreateEvent> implements ComponentRow {
	protected final String id;
	public final Class<T> type;

	public Component(@NotNull Class<T> type, String id) {
		Checks.notNull(type, "type");
		this.type = type;
		this.id = id;
	}

	@Override
	public List<Component<?>> getComponents() {
		return Collections.singletonList(this);
	}

	public abstract ActionComponent buildComponent(MenuBase menu);

	public abstract void handle(MenuBase menu, GenericComponentInteractionCreateEvent event);

	public CompletableFuture<T> createHandler(MenuBase menu) {
		var future = menu.getEventManager().waitForEvent(type, event -> event.getComponentId().equals(id), Menu.timeout);

		future.whenComplete((event, e) -> {
			if(event != null) {
				menu.handle(event);

				try {
					handle(menu, event);
				} catch(Exception ex) {
					Menu.logger.error("Component execution failed", ex);
				}
			} else if(e instanceof TimeoutException) menu.close();

		});

		return future;
	}
}
