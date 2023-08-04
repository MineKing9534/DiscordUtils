package de.mineking.discord.ui;

import de.mineking.discord.events.EventManager;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public interface MenuBase {
	<T> void putData(String name, T object);
	<T> T getData(String name, Class<T> type);

	EventManager getEventManager();

	default void handle(GenericInteractionCreateEvent event) {
	}

	String getId();

	void update();

	void display(String name);

	default void close() {
		close(true);
	}

	void close(boolean delete);
}
