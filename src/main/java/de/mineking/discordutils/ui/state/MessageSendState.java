package de.mineking.discordutils.ui.state;

import com.google.gson.JsonObject;
import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.RenderTermination;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class MessageSendState extends SendState<MessageMenu> {
	public MessageSendState(@NotNull MessageMenu menu, JsonObject data) {
		super(menu, data);
	}

	@Override
	public void display(@NotNull GenericComponentInteractionCreateEvent event) {
		display((IReplyCallback) event);
	}

	/**
	 * @param channel The channel to send the menu in
	 */
	public void display(@NotNull MessageChannel channel) {
		Checks.notNull(channel, "channel");

		menu.components.stream()
				.flatMap(r -> r.getComponents().stream())
				.forEach(c -> c.register(this));

		try {
			channel.sendMessage(MessageCreateData.fromEditData(menu.buildMessage(new UpdateState(null, menu, data)))).queue();
		} catch(RenderTermination ignored) {
		}
	}

	/**
	 * @param event     An {@link IReplyCallback} to reply the menu to
	 * @param ephemeral Whether the message should only be visible to the user who created the interaction
	 */
	public void display(@NotNull IReplyCallback event, boolean ephemeral) {
		Checks.notNull(event, "event");

		menu.components.stream()
				.flatMap(r -> r.getComponents().stream())
				.forEach(c -> c.register(this));

		try {
			if(event.isAcknowledged()) event.getHook().editOriginal(menu.buildMessage(new UpdateState(event, menu, data))).queue();
			else event.reply(MessageCreateData.fromEditData(menu.buildMessage(new UpdateState(event, menu, data)))).setEphemeral(ephemeral).queue();
		} catch(RenderTermination ignored) {
		}
	}

	/**
	 * @param event An {@link IReplyCallback} to reply the menu to
	 */
	public void display(@NotNull IReplyCallback event) {
		display(event, true);
	}

	@NotNull
	@Override
	public <T> MessageSendState setState(@NotNull String name, @Nullable T value) {
		return (MessageSendState) super.setState(name, value);
	}

	@NotNull
	@Override
	public <T> MessageSendState setState(@NotNull String name, @NotNull Function<T, T> value) {
		return (MessageSendState) super.setState(name, value);
	}

	@NotNull
	@Override
	public MessageSendState putStates(@NotNull Map<String, ?> states) {
		return (MessageSendState) super.putStates(states);
	}
}
