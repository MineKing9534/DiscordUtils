package de.mineking.discordutils.ui.state;

import com.google.gson.JsonObject;
import de.mineking.discordutils.ui.Menu;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SendState<M extends Menu> extends DataState<M> {
	protected final Set<Consumer<DataState<M>>> setup = new HashSet<>();

	public SendState(@NotNull M menu, JsonObject data) {
		super(null, menu, data);
	}

	/**
	 * @param event The event to reply the menu to
	 */
	public abstract void display(@NotNull GenericComponentInteractionCreateEvent event);

	@NotNull
	@Override
	public <T> SendState<M> setState(@NotNull String name, @Nullable T value) {
		return (SendState<M>) super.setState(name, value);
	}

	@NotNull
	@Override
	public <T> SendState<M> setState(@NotNull String name, @NotNull Function<T, T> value) {
		return (SendState<M>) super.setState(name, value);
	}

	@NotNull
	@Override
	public SendState<M> putStates(@NotNull Map<String, ?> states) {
		return (SendState<M>) super.putStates(states);
	}
}
