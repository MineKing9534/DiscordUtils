package de.mineking.discord.events.interaction;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.function.Consumer;

public class StringSelectHandler extends SelectionHandler<String, StringSelectMenu, StringSelectInteractionEvent> {
	public StringSelectHandler(String id, Consumer<StringSelectInteractionEvent> handler) {
		super(StringSelectInteractionEvent.class, id, handler);
	}
}
