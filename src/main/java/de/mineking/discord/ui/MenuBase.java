package de.mineking.discord.ui;

import de.mineking.discord.events.EventManager;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public interface MenuBase {
	EventManager getEventManager();

	default void handle(GenericInteractionCreateEvent event) {}

	String getId();

	void update();

	void display(String name);

	void close();
}
