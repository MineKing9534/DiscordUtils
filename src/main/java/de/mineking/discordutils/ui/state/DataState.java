package de.mineking.discordutils.ui.state;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.mineking.discordutils.ui.Menu;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class DataState extends State {
	protected final IReplyCallback event;

	DataState(@Nullable IReplyCallback event, @NotNull Menu menu, @NotNull JsonObject data) {
		super(menu, data);

		this.event = event;
	}

	/**
	 * @return An {@link Optional} holding the last interaction event
	 */
	@NotNull
	public Optional<IReplyCallback> getEvent() {
		return Optional.ofNullable(event);
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name The name of the state
	 * @return The state value or {@code null}
	 */
	@Nullable
	public <T> T getNullableState(@NotNull String name) {
		Checks.notNull(name, "name");

		var element = data.get(name);
		if(element == null) return null;

		return gson.fromJson(element, new TypeToken<>() {
		});
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name The name of the state
	 * @return An {@link Optional} holding the state value
	 */
	@NotNull
	public <T> Optional<T> getOptionalState(@NotNull String name) {
		return Optional.ofNullable(this.getNullableState(name));
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name The name of the state
	 * @return The value of the state. If there is no value present, an exception is thrown
	 */
	@NotNull
	public <T> T getState(@NotNull String name) {
		return this.<T>getOptionalState(name).orElseThrow();
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name    The name of the state
	 * @param creator A function to convert the state to the wanted type
	 * @return The value or {@code null}
	 */
	@Nullable
	public <U, T> T getNullableState(@NotNull String name, @NotNull Function<U, T> creator) {
		Checks.notNull(name, "name");
		Checks.notNull(creator, "creator");

		var temp = this.<U>getNullableState(name);
		if(temp == null) return null;

		return creator.apply(temp);
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name    The name of the state
	 * @param creator A function to convert the state to the wanted type
	 * @return An {@link Optional} holding the state value
	 */
	@NotNull
	public <U, T> Optional<T> getOptionalState(@NotNull String name, @NotNull Function<U, T> creator) {
		return Optional.ofNullable(this.getNullableState(name, creator));
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name    The name of the state
	 * @param creator A function to convert the state to the wanted type
	 * @return The value of the state. If there is no value present, an exception is thrown.
	 */
	@NotNull
	public <U, T> T getState(@NotNull String name, @NotNull Function<U, T> creator) {
		return getOptionalState(name, creator).orElseThrow();
	}

	@NotNull
	@Override
	public <T> DataState setState(@NotNull String name, @Nullable T value) {
		return (DataState) super.setState(name, value);
	}

	@NotNull
	@Override
	public <T> DataState setState(@NotNull String name, @NotNull Function<T, T> value) {
		Checks.notNull(name, "name");
		Checks.notNull(value, "value");

		var currentValue = this.<T>getNullableState(name);
		var newValue = value.apply(currentValue);

		if(newValue != null) data.add(name, gson.toJsonTree(newValue));
		else data.remove(name);

		if(!Objects.equals(newValue, currentValue)) menu.triggerEffect(name, newValue);

		return this;
	}
}
