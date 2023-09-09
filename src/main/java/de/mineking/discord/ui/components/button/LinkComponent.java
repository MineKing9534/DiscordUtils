package de.mineking.discord.ui.components.button;

import de.mineking.discord.ui.MenuBase;
import de.mineking.discord.ui.components.BaseComponent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.concurrent.CompletableFuture;

public class LinkComponent extends BaseComponent<ButtonInteractionEvent> {
	private final String url;
	private final ButtonLabel label;

	public LinkComponent(String url, String label) {
		super(ButtonInteractionEvent.class, "");
		this.url = url;
		this.label = new ButtonLabel(label);
	}

	public LinkComponent(String url, Emoji label) {
		super(ButtonInteractionEvent.class, "");
		this.url = url;
		this.label = new ButtonLabel(label);
	}

	@Override
	public Button getComponent(String id, MenuBase menu) {
		return label.build(ButtonStyle.LINK, url);
	}

	@Override
	public void handleParsed(MenuBase menu, ButtonInteractionEvent event) {
		//Ignore. Link buttons cannot be pressed
	}

	@Override
	public CompletableFuture<ButtonInteractionEvent> createHandler(MenuBase menu) {
		return CompletableFuture.completedFuture(null);
	}
}
