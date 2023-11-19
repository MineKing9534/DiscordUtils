package de.mineking.discordutils.ui.state;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.mineking.discordutils.ui.Menu;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class SendState extends State {
	public SendState(@NotNull Menu menu, JsonObject data) {
		super(menu, data);
	}

	/**
	 * @param channel The channel to send the menu in
	 */
	public void display(@NotNull MessageChannel channel) {
		Checks.notNull(channel, "channel");

		menu.components.stream()
				.flatMap(r -> r.getComponents().stream())
				.forEach(c -> c.register(this));

		channel.sendMessage(MessageCreateData.fromEditData(menu.buildMessage(new UpdateState(null, menu, data)))).queue();
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

		if(event.isAcknowledged()) event.getHook().editOriginal(menu.buildMessage(new UpdateState(event, menu, data))).queue();
		else event.reply(MessageCreateData.fromEditData(menu.buildMessage(new UpdateState(event, menu, data)))).setEphemeral(ephemeral).queue();
	}

	/**
	 * @param event An {@link IReplyCallback} to reply the menu to
	 */
	public void display(@NotNull IReplyCallback event) {
		display(event, true);
	}

	@NotNull
	@Override
	public <T> SendState setState(@NotNull String name, @Nullable T value) {
		return (SendState) super.setState(name, value);
	}

	@NotNull
	@Override
	public <T> SendState setState(@NotNull String name, @NotNull Function<T, T> value) {
		Checks.notNull(name, "name");
		Checks.notNull(value, "value");

		var element = data.get(name);

		var currentValue = element == null ? null : gson.fromJson(element, new TypeToken<T>() {
		});
		var newValue = value.apply(currentValue);

		if(newValue != null) data.add(name, gson.toJsonTree(newValue));
		else data.remove(name);

		if(!Objects.equals(newValue, currentValue)) menu.triggerEffect(name, newValue);

		return this;
	}
}
