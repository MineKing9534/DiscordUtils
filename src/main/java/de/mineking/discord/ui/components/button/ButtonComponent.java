package de.mineking.discord.ui.components.button;

import de.mineking.discord.ui.MenuBase;
import de.mineking.discord.ui.components.BaseComponent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ButtonComponent extends BaseComponent<ButtonInteractionEvent> {
	public ButtonComponent(String id, ButtonColor color, String label) {
		super(ButtonInteractionEvent.class, i -> Button.of(color.style, i + ":" + id, label));
	}

	public ButtonComponent(String id, ButtonColor color, Emoji label) {
		super(ButtonInteractionEvent.class, i -> Button.of(color.style, i + ":" + id, label));
	}

	public ButtonComponent handle(BiConsumer<MenuBase, ButtonInteractionEvent> handler) {
		this.handler = handler;
		return this;
	}

	public ButtonComponent handle(Consumer<ButtonInteractionEvent> handler) {
		this.handler = (menu, event) -> handler.accept(event);
		return this;
	}
}
