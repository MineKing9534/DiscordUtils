package de.mineking.discordutils.ui.state;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.mineking.discordutils.ui.Menu;
import de.mineking.discordutils.ui.modal.ModalMenu;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataState<M extends Menu> extends State<M> {
	protected final Map<String, Object> cache = new HashMap<>();
	protected final IReplyCallback event;

	public DataState(@Nullable IReplyCallback event, @NotNull M menu, @NotNull JsonObject data) {
		super(menu, data);

		this.event = event;
	}

	/**
	 * @param menu  The parent {@link ModalMenu}
	 * @param event The {@link GenericComponentInteractionCreateEvent}
	 * @return A loaded {@link UpdateState}
	 */
	@NotNull
	public static DataState<ModalMenu> load(@NotNull ModalMenu menu, @NotNull ModalInteractionEvent event) {
		Checks.notNull(menu, "menu");
		Checks.notNull(event, "event");

		var data = new StringBuilder();

		data.append(event.getModalId().split(":", 2)[1]);

		event.getValues().forEach(c -> data.append(c.getId().split(":", 2)[1]));

		var result = new DataState<>(event, menu, JsonParser.parseString(data.toString()).getAsJsonObject());
		menu.initialize(result);
		return result;
	}

	/**
	 * @return An {@link Optional} holding the last interaction event
	 */
	@NotNull
	public Optional<IReplyCallback> event() {
		return Optional.ofNullable(event);
	}

	/**
	 * @return The last interaction event or throws an exception if none is present
	 */
	@NotNull
	public IReplyCallback getEvent() {
		return event().orElseThrow();
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name The name of the state
	 * @param type The {@link Type} of the state
	 * @return The state value or {@code null}
	 */
	@Nullable
	public <T> T getNullableState(@NotNull String name, @NotNull Type type) {
		Checks.notNull(name, "name");

		var element = data.get(name);
		if(element == null) return null;

		return gson.fromJson(element, type);
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name The name of the state
	 * @param type The {@link Class} of the state
	 * @return The state value or {@code null}
	 */
	@Nullable
	public <T> T getNullableState(@NotNull String name, @NotNull Class<T> type) {
		return getNullableState(name, (Type) type);
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name The name of the state
	 * @param type The {@link Type} of the state
	 * @return An {@link Optional} holding the state value
	 */
	@NotNull
	public <T> Optional<T> getOptionalState(@NotNull String name, @NotNull Type type) {
		return Optional.ofNullable(this.getNullableState(name, type));
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name The name of the state
	 * @param type The {@link Class} of the state
	 * @return An {@link Optional} holding the state value
	 */
	@NotNull
	public <T> Optional<T> getOptionalState(@NotNull String name, @NotNull Class<T> type) {
		return Optional.ofNullable(this.getNullableState(name, type));
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name The name of the state
	 * @param type The {@link Type} of the state
	 * @return The value of the state. If there is no value present, an exception is thrown
	 */
	@NotNull
	public <T> T getState(@NotNull String name, @NotNull Type type) {
		return this.<T>getOptionalState(name, type).orElseThrow();
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name The name of the state
	 * @param type The {@link Class} of the state
	 * @return The value of the state. If there is no value present, an exception is thrown
	 */
	@NotNull
	public <T> T getState(@NotNull String name, @NotNull Class<T> type) {
		return getOptionalState(name, type).orElseThrow();
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name    The name of the state
	 * @param type    The {@link Type} of the state
	 * @param creator A function to convert the state to the wanted type
	 * @return The value or {@code null}
	 */
	@Nullable
	public <U, T> T getNullableState(@NotNull String name, @NotNull Type type, @NotNull Function<U, T> creator) {
		Checks.notNull(name, "name");
		Checks.notNull(creator, "creator");

		var temp = this.<U>getNullableState(name, type);
		if(temp == null) return null;

		return creator.apply(temp);
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name    The name of the state
	 * @param type    The {@link Type} of the state
	 * @param creator A function to convert the state to the wanted type
	 * @return An {@link Optional} holding the state value
	 */
	@NotNull
	public <U, T> Optional<T> getOptionalState(@NotNull String name, @NotNull Type type, @NotNull Function<U, T> creator) {
		return Optional.ofNullable(this.getNullableState(name, type, creator));
	}

	/**
	 * States are json-serialized and then saved in the empty space of component ids. This means that state storage is very limited. Therefore, you should only store data in the state that you really need to.
	 *
	 * @param name    The name of the state
	 * @param type    The {@link Type} of the state
	 * @param creator A function to convert the state to the wanted type
	 * @return The value of the state. If there is no value present, an exception is thrown.
	 */
	@NotNull
	public <U, T> T getState(@NotNull String name, @NotNull Type type, @NotNull Function<U, T> creator) {
		return getOptionalState(name, type, creator).orElseThrow();
	}

	@NotNull
	@Override
	public <T> DataState<M> setState(@NotNull String name, @Nullable T value) {
		return (DataState<M>) super.setState(name, value);
	}

	@NotNull
	@Override
	public <T> DataState<M> setState(@NotNull String name, @NotNull Type type, @NotNull Function<T, T> value) {
		Checks.notNull(name, "name");
		Checks.notNull(value, "value");

		var currentValue = this.<T>getNullableState(name, type);
		var newValue = value.apply(currentValue);

		if(newValue != null) data.add(name, gson.toJsonTree(newValue));
		else data.remove(name);

		if(!Objects.equals(newValue, currentValue)) menu.triggerEffect(this, name, currentValue, newValue);

		return this;
	}

	@NotNull
	@Override
	public DataState<M> putStates(@NotNull Map<String, ?> states) {
		return (DataState<M>) super.putStates(states);
	}

	/**
	 * @param name  The name of the cache value
	 * @param value The value to cache. The cache is <u>not</u> persisted across re-renders!
	 * @return {@code this}
	 */
	@NotNull
	public <T> DataState<M> setCache(@NotNull String name, @Nullable T value) {
		Checks.notNull(name, "name");

		cache.put(name, value);
		return this;
	}

	/**
	 * @param cache The values you want to add to the cache
	 * @return {@code this}
	 */
	@NotNull
	public DataState<M> putCaches(@NotNull Map<String, ?> cache) {
		Checks.notNull(cache, "cache");

		this.cache.putAll(cache);
		return this;
	}

	/**
	 * @param name The name of the cache to get
	 * @return The cache value or {@code null}
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public <T> T getCache(@NotNull String name) {
		Checks.notNull(name, "name");
		return (T) cache.get(name);
	}

	/**
	 * @param name     The name of the cache to get
	 * @param provider A function to provide a value if none is present yet
	 * @return The value of the cache
	 */
	@NotNull
	public <T> T getCache(@NotNull String name, @NotNull Supplier<T> provider) {
		Checks.notNull(name, "name");

		var value = this.<T>getCache(name);

		if(value == null) {
			var temp = provider.get();
			value = temp;
			cache.put(name, temp);
		}

		return value;
	}
}
