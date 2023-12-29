package de.mineking.discordutils.console;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

public interface RedirectTarget {
	void sendMessage(@NotNull JDA jda, @NotNull MessageCreateData message);

	/**
	 * @param channel The channel to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified channel
	 */
	@NotNull
	static RedirectTarget channel(long channel) {
		return (jda, message) -> jda.getChannelById(MessageChannel.class, channel)
				.sendMessage(message)
				.queue();
	}

	/**
	 * @param user The user to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified user
	 */
	@NotNull
	static RedirectTarget directMessage(long user) {
		return (jda, message) -> jda.openPrivateChannelById(user)
				.flatMap(channel -> channel.sendMessage(message))
				.queue();
	}

	@NotNull
	static RedirectTarget webhook(@NotNull String url) {
		Checks.notNull(url, "url");

		return new RedirectTarget() {
			private final JDAWebhookClient webhook = new WebhookClientBuilder(url).buildJDA();

			@Override
			public void sendMessage(@NotNull JDA jda, @NotNull MessageCreateData message) {
				webhook.send(WebhookMessageBuilder.fromJDA(message).build());
			}
		};
	}
}
