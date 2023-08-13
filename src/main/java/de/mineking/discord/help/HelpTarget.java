package de.mineking.discord.help;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public interface HelpTarget {
	String getKey();

	MessageCreateData build(IReplyCallback event);

	default String getDisplay(DiscordLocale locale) {
		return getKey();
	}

	default boolean matches(String current) {
		return getKey().toLowerCase().startsWith(current.toLowerCase());
	}

	default boolean isAvailable(GenericInteractionCreateEvent event) {
		return true;
	}
}
