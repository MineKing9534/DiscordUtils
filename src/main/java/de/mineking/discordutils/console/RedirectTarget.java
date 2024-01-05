package de.mineking.discordutils.console;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface RedirectTarget {
	void sendMessage(@NotNull JDA jda, @NotNull MessageCreateBuilder message);

	/**
	 * @param channel The channel to send the log messages to
	 * @param handler A {@link Consumer} to configure the message@param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified channel
	 */
	@NotNull
	static RedirectTarget channel(@NotNull MessageChannel channel, @Nullable Consumer<MessageCreateBuilder> handler) {
		return (jda, message) -> channel
				.sendMessage(prepare(message, handler))
				.queue();
	}

	/**
	 * @param channel The channel to send the log messages to
	 * @param handler A {@link Consumer} to configure the message@param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified channel
	 */
	@NotNull
	static RedirectTarget channel(long channel, @Nullable Consumer<MessageCreateBuilder> handler) {
		return (jda, message) -> jda.getChannelById(MessageChannel.class, channel)
				.sendMessage(prepare(message, handler))
				.queue();
	}

	/**
	 * @param channel The channel to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified channel
	 */
	@NotNull
	static RedirectTarget channel(@NotNull MessageChannel channel) {
		return channel(channel, null);
	}

	/**
	 * @param channel The channel to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified channel
	 */
	@NotNull
	static RedirectTarget channel(long channel) {
		return channel(channel, null);
	}

	/**
	 * @param user    The user to send the log messages to
	 * @param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified user
	 */
	@NotNull
	static RedirectTarget directMessage(long user, @Nullable Consumer<MessageCreateBuilder> handler) {
		return (jda, message) -> jda.openPrivateChannelById(user)
				.flatMap(channel -> channel.sendMessage(prepare(message, handler)))
				.queue();
	}

	/**
	 * @param user    The user to send the log messages to
	 * @param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified user
	 */
	@NotNull
	static RedirectTarget directMessage(@NotNull UserSnowflake user, @Nullable Consumer<MessageCreateBuilder> handler) {
		Checks.notNull(user, "user");
		return directMessage(user.getIdLong(), handler);
	}

	/**
	 * @param user The user to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified user
	 */
	@NotNull
	static RedirectTarget directMessage(long user) {
		Checks.notNull(user, "user");
		return directMessage(user, null);
	}

	/**
	 * @param user The user to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified user
	 */
	@NotNull
	static RedirectTarget directMessage(@NotNull UserSnowflake user) {
		Checks.notNull(user, "user");
		return directMessage(user, null);
	}

	/**
	 * @param url     Your webhook url (including token)
	 * @param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified webhook
	 */
	@NotNull
	static RedirectTarget webhook(@NotNull String url, @Nullable Consumer<MessageCreateBuilder> handler) {
		Checks.notNull(url, "url");

		return new RedirectTarget() {
			private final JDAWebhookClient webhook = new WebhookClientBuilder(url).buildJDA();

			@Override
			public void sendMessage(@NotNull JDA jda, @NotNull MessageCreateBuilder message) {
				webhook.send(WebhookMessageBuilder.fromJDA(RedirectTarget.prepare(message, handler)).build());
			}
		};
	}

	/**
	 * @param url Your webhook url (including token)
	 * @return A {@link RedirectTarget} that sends all log messages to the specified webhook
	 */
	@NotNull
	static RedirectTarget webhook(@NotNull String url) {
		return webhook(url, null);
	}

	@NotNull
	private static MessageCreateData prepare(@NotNull MessageCreateBuilder message, @Nullable Consumer<MessageCreateBuilder> handler) {
		if(handler != null) handler.accept(message);
		return message.build();
	}
}
