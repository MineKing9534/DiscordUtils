package de.mineking.discord.ui.components;

import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

public class DefaultComponent extends BaseComponent<GenericComponentInteractionCreateEvent> {
	public DefaultComponent(ActionComponent component) {
		super(GenericComponentInteractionCreateEvent.class, i -> component);
	}
}
