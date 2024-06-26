package de.mineking.discordutils.ui.modal;

import com.google.gson.JsonObject;
import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.SendState;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

public class ModalSendState extends SendState<ModalMenu> {
	public ModalSendState(@NotNull ModalMenu menu, JsonObject data) {
		super(menu, data);
	}

	@Override
	public void display(@NotNull GenericComponentInteractionCreateEvent event) {
		display((IModalCallback) event);
	}

	/**
	 * @param event The {@link IModalCallback} to reply the modal to
	 */
	public void display(@NotNull IModalCallback event) {
		Checks.notNull(event, "event");
		event.replyModal(menu.buildModal(prepareState(new DataState<>((IReplyCallback) event, menu, data)))).queue();
	}

	private DataState<ModalMenu> prepareState(DataState<ModalMenu> state) {
		setup.forEach(f -> f.accept(state));
		state.putCaches(cache);

		return state;
	}

	@NotNull
	@Override
	public <T> ModalSendState setState(@NotNull String name, @Nullable T value) {
		return (ModalSendState) super.setState(name, value);
	}

	@NotNull
	@Override
	public <T> ModalSendState setState(@NotNull String name, @NotNull Class<T> type, @NotNull Function<T, T> value) {
		return (ModalSendState) super.setState(name, type, value);
	}

	@NotNull
	@Override
	public <T> ModalSendState setState(@NotNull String name, @NotNull Type type, @NotNull Function<T, T> value) {
		return (ModalSendState) super.setState(name, type, value);
	}

	@NotNull
	@Override
	public ModalSendState putStates(@NotNull Map<String, ?> states) {
		return (ModalSendState) super.putStates(states);
	}

	@NotNull
	@Override
	public <T> ModalSendState setCache(@NotNull String name, @Nullable T value) {
		return (ModalSendState) super.setCache(name, value);
	}

	@NotNull
	@Override
	public ModalSendState putCaches(@NotNull Map<String, ?> cache) {
		return (ModalSendState) super.putCaches(cache);
	}
}
