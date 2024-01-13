package de.mineking.discordutils.console;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.mineking.discordutils.DiscordUtils;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface RedirectTarget<B> {
	void sendMessage(@NotNull DiscordUtils<B> discordUtils, @NotNull MessageCreateBuilder message);

	@NotNull
	static <B> RedirectTarget<B> pingRoleOnError(@NotNull RedirectTarget<B> original, @NotNull Function<B, Optional<Role>> role) {
		return (discordUtils, message) -> {
			Optional<Role> r = role.apply(discordUtils.getBot());
			original.sendMessage(discordUtils, r.isPresent() && message.getContent().contains("ERROR") ? message.setContent(r.get().getAsMention() + message.getContent()) : message);
		};
	}

	/**
	 * @param channel The channel to send the log messages to
	 * @param handler A {@link Consumer} to configure the message@param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified channel
	 */
	@NotNull
	static <B> RedirectTarget<B> channel(@NotNull MessageChannel channel, @Nullable Consumer<MessageCreateBuilder> handler) {
		return (discordUtils, message) -> channel.sendMessage(prepare(message, handler)).queue();
	}

	/**
	 * @param channel The channel to send the log messages to
	 * @param handler A {@link Consumer} to configure the message@param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified channel
	 */
	@NotNull
	static <B> RedirectTarget<B> channel(long channel, @Nullable Consumer<MessageCreateBuilder> handler) {
		return (discordUtils, message) -> discordUtils.jda.getChannelById(MessageChannel.class, channel).sendMessage(prepare(message, handler)).queue();
	}

	/**
	 * @param channel The channel to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified channel
	 */
	@NotNull
	static <B> RedirectTarget<B> channel(@NotNull MessageChannel channel) {
		return channel(channel, null);
	}

	/**
	 * @param channel The channel to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified channel
	 */
	@NotNull
	static <B> RedirectTarget<B> channel(long channel) {
		return channel(channel, null);
	}

	/**
	 * @param user    The user to send the log messages to
	 * @param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified user
	 */
	@NotNull
	static <B> RedirectTarget<B> directMessage(long user, @Nullable Consumer<MessageCreateBuilder> handler) {
		return (discordUtils, message) -> discordUtils.jda.openPrivateChannelById(user).flatMap(channel -> channel.sendMessage(prepare(message, handler))).queue();
	}

	/**
	 * @param user    The user to send the log messages to
	 * @param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified user
	 */
	@NotNull
	static <B> RedirectTarget<B> directMessage(@NotNull UserSnowflake user, @Nullable Consumer<MessageCreateBuilder> handler) {
		Checks.notNull(user, "user");
		return directMessage(user.getIdLong(), handler);
	}

	/**
	 * @param user The user to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified user
	 */
	@NotNull
	static <B> RedirectTarget<B> directMessage(long user) {
		Checks.notNull(user, "user");
		return directMessage(user, null);
	}

	/**
	 * @param user The user to send the log messages to
	 * @return A {@link RedirectTarget} that sends all log messages to the specified user
	 */
	@NotNull
	static <B> RedirectTarget<B> directMessage(@NotNull UserSnowflake user) {
		Checks.notNull(user, "user");
		return directMessage(user, null);
	}

	/**
	 * @param url     Your webhook url (including token)
	 * @param handler A {@link Consumer} to configure the message
	 * @return A {@link RedirectTarget} that sends all log messages to the specified webhook
	 */
	@NotNull
	static <B> RedirectTarget<B> webhook(@NotNull String url, @Nullable Consumer<MessageCreateBuilder> handler) {
		Checks.notNull(url, "url");

		return new RedirectTarget<>() {
			private final JDAWebhookClient webhook = new WebhookClientBuilder(url).buildJDA();

			@Override
			public void sendMessage(@NotNull DiscordUtils<B> discordUtils, @NotNull MessageCreateBuilder message) {
				webhook.send(WebhookMessageBuilder.fromJDA(RedirectTarget.prepare(message, handler)).build());
			}
		};
	}

	/**
	 * @param url Your webhook url (including token)
	 * @return A {@link RedirectTarget} that sends all log messages to the specified webhook
	 */
	@NotNull
	static <B> RedirectTarget<B> webhook(@NotNull String url) {
		return webhook(url, null);
	}

	@NotNull
	private static MessageCreateData prepare(@NotNull MessageCreateBuilder message, @Nullable Consumer<MessageCreateBuilder> handler) {
		if(handler != null) handler.accept(message);
		return message.build();
	}
}
