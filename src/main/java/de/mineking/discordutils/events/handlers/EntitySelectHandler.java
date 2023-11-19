package de.mineking.discordutils.events.handlers;

import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EntitySelectHandler extends ComponentHandler<EntitySelectInteractionEvent> {
	/**
	 * @param filter  Regex for custom id
	 * @param handler The handler
	 */
	public EntitySelectHandler(@NotNull String filter, @NotNull Consumer<EntitySelectInteractionEvent> handler) {
		super(EntitySelectInteractionEvent.class, filter, handler);
	}
}
