package de.mineking.discord.commands.interaction.handler;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public abstract class ButtonHandler extends InteractionHandler<ButtonInteractionEvent, Button> {
	public ButtonHandler(boolean autoRemove) {
		super(autoRemove);
	}

	public ButtonHandler() {
		this(false);
	}
	
	@Override
	protected final Button getArguments(ButtonInteractionEvent event) {
		return event.getButton();
	}
}
