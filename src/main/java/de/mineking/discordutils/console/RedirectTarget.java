package de.mineking.discordutils.console;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

public interface RedirectTarget {
	void sendMessage(JDA jda, MessageCreateData message);

	@NotNull
	static RedirectTarget channel(long id) {
		return (jda, message) -> jda.getChannelById(MessageChannel.class, id)
				.sendMessage(message)
				.queue();
	}

	@NotNull
	static RedirectTarget directMessage(@NotNull UserSnowflake user) {
		return (jda, message) -> jda.openPrivateChannelById(user.getIdLong())
				.flatMap(channel -> channel.sendMessage(message))
				.queue();
	}
}
