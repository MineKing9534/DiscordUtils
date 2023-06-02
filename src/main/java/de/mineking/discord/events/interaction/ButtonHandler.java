package de.mineking.discord.events.interaction;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ButtonHandler extends ComponentHandler<ButtonInteractionEvent> {
	public ButtonHandler(Predicate<ButtonInteractionEvent> filter, Consumer<ButtonInteractionEvent> handler) {
		super(ButtonInteractionEvent.class, filter, handler);
	}

	public ButtonHandler(String id, Consumer<ButtonInteractionEvent> handler) {
		super(ButtonInteractionEvent.class, id, handler);
	}
}
