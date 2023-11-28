package de.mineking.discordutils.ui.state;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.mineking.discordutils.ui.Menu;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class SendState<M extends Menu> extends State<M> {
	public SendState(@NotNull M menu, JsonObject data) {
		super(menu, data);
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
		Checks.notNull(name, "name");
		Checks.notNull(value, "value");

		var element = data.get(name);

		var currentValue = element == null ? null : gson.fromJson(element, new TypeToken<T>() {
		});
		var newValue = value.apply(currentValue);

		if(newValue != null) data.add(name, gson.toJsonTree(newValue));
		else data.remove(name);

		if(!Objects.equals(newValue, currentValue)) menu.triggerEffect(this, name, currentValue, newValue);

		return this;
	}

	@NotNull
	@Override
	public SendState<M> putStates(@NotNull Map<String, ?> states) {
		return (SendState<M>) super.putStates(states);
	}
}
