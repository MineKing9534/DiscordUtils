package de.mineking.discordutils.ui.state;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.mineking.discordutils.ui.MessageMenu;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

public class UpdateState extends DataState<MessageMenu> {
	UpdateState(@Nullable IReplyCallback event, @NotNull MessageMenu menu, @NotNull JsonObject data) {
		super(event, menu, data);
		menu.initialize(this);
	}

	/**
	 * @param menu  The parent {@link MessageMenu}
	 * @param event The {@link GenericComponentInteractionCreateEvent}
	 * @return A loaded {@link UpdateState}
	 */
	@NotNull
	public static UpdateState load(@NotNull MessageMenu menu, @NotNull GenericComponentInteractionCreateEvent event) {
		Checks.notNull(menu, "menu");
		Checks.notNull(event, "event");

		var data = new StringBuilder();

		event.getMessage().getComponents().stream().flatMap(r -> r.getComponents().stream()).filter(c -> c instanceof ActionComponent).map(c -> (ActionComponent) c).filter(c -> c.getId() != null).forEach(c -> data.append(c.getId().split(":", 3)[2]));

		return new UpdateState(event, menu, JsonParser.parseString(data.toString()).getAsJsonObject());
	}

	/**
	 * Re-renders the menu
	 */
	public void instantUpdate() {
		if(event == null) return;

		if(event instanceof IMessageEditCallback edit && !edit.isAcknowledged())
			edit.editMessage(menu.buildMessage(this)).queue();
		else event.getHook().editOriginal(menu.buildMessage(this)).queue();
	}

	/**
	 * Disables all components and waits for an actual rerender using {@link #instantUpdate()}
	 */
	public void deferUpdate() {
		if (event == null || !(event instanceof GenericComponentInteractionCreateEvent evt)) return;

		var components = evt.getMessage().getComponents().stream().map(a -> ActionRow.of(a.getComponents().stream().map(c -> c instanceof ActionComponent ac ? ac.withDisabled(true) : c).toList())).toList();

		if (!evt.isAcknowledged()) evt.editComponents(components).queue();
		else evt.getHook().editOriginalComponents(components).queue();
	}

	/**
	 * Disables all components and then re-renders the menu. This should be used if your rendering takes very long
	 */
	public void update() {
		deferUpdate();
		instantUpdate();
	}

	/**
	 * Deletes the menu message
	 */
	public void close() {
		event().ifPresent(event -> event.getHook().deleteOriginal().queue());
	}

	/**
	 * @param message The message to reply
	 */
	public void sendReply(@NotNull MessageCreateData message) {
		Checks.notNull(message, "message");
		event().ifPresent(event -> {
			if(event.isAcknowledged()) event.getHook().sendMessage(message).setEphemeral(true).queue();
			else event.reply(message).setEphemeral(true).queue();
		});
	}

	@NotNull
	@Override
	public <T> UpdateState setState(@NotNull String name, @Nullable T value) {
		return (UpdateState) super.setState(name, value);
	}

	@NotNull
	@Override
	public <T> UpdateState setState(@NotNull String name, @NotNull Class<T> type, @NotNull Function<T, T> value) {
		return (UpdateState) super.setState(name, type, value);
	}

	@NotNull
	@Override
	public <T> UpdateState setState(@NotNull String name, @NotNull Type type, @NotNull Function<T, T> value) {
		return (UpdateState) super.setState(name, type, value);
	}

	@NotNull
	@Override
	public <T> UpdateState setCache(@NotNull String name, @Nullable T value) {
		return (UpdateState) super.setCache(name, value);
	}

	@NotNull
	@Override
	public UpdateState putCaches(@NotNull Map<String, ?> cache) {
		return (UpdateState) super.putCaches(cache);
	}
}
