package de.mineking.discord.ui.components.button;

import de.mineking.discord.ui.MenuBase;
import de.mineking.discord.ui.components.HandleComponent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ButtonComponent extends HandleComponent<ButtonInteractionEvent> {
	private final ButtonColor color;
	private final ButtonLabel label;

	public ButtonComponent(String id, ButtonColor color, String label) {
		super(ButtonInteractionEvent.class, id);
		this.color = color;
		this.label = new ButtonLabel(label);
	}

	public ButtonComponent(String id, ButtonColor color, Emoji label) {
		super(ButtonInteractionEvent.class, id);
		this.color = color;
		this.label = new ButtonLabel(label);
	}

	@Override
	public Button getComponent(String id, MenuBase menu) {
		return label.build(color.style, id);
	}
}
