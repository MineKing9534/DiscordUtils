package de.mineking.discord.ui.components.button;

import de.mineking.discord.ui.components.BaseComponent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class LinkComponent extends BaseComponent<ButtonInteractionEvent> {
	public LinkComponent(String url, String label) {
		super(ButtonInteractionEvent.class, id -> Button.link(url, label));
	}

	public LinkComponent(String url, Emoji label) {
		super(ButtonInteractionEvent.class, id -> Button.link(url, label));
	}
}
