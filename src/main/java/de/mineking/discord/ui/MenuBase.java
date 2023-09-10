package de.mineking.discord.ui;

import de.mineking.discord.events.EventManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public interface MenuBase {
	<T> void putData(String name, T object);
	<T> T getData(String name, Class<T> type);

	EventManager getEventManager();

	default void handle(GenericInteractionCreateEvent event) {
	}

	String getId();

	void update();

	void display(String name);

	MenuBase setLoading();

	default void close() {
		close(true);
	}

	void close(boolean delete);

	CallbackState getState();

	default IReplyCallback getEvent() {
		return getState().reply;
	}

	default Member getMember() {
		return getEvent().getMember();
	}

	default Guild getGuild() {
		return getEvent().getGuild();
	}

	default DiscordLocale getLocale() {
		return getEvent().getUserLocale();
	}
}
