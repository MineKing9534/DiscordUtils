package de.mineking.discordutils.ui.state;

import com.google.gson.*;
import de.mineking.discordutils.ui.Menu;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public abstract class State<M extends Menu> {
	public static ToNumberStrategy numberStrategy = in -> {
		var str = in.nextString();
		return str.contains(".") ? Double.parseDouble(str) : Integer.parseInt(str);
	};

	public final static Gson gson = new GsonBuilder().setNumberToNumberStrategy(numberStrategy).setObjectToNumberStrategy(numberStrategy).create();

	private final M menu;

	private final JsonObject data;

	State(@NotNull M menu, @NotNull JsonObject data) {
		Checks.notNull(menu, "menu");
		Checks.notNull(data, "data");

		this.menu = menu;
		this.data = data;
	}

	/**
	 * @return The menu owning this state
	 */
	@NotNull
	public M getMenu() {
		return menu;
	}

	/**
	 * @return The {@link JsonObject} holding the state data
	 */
	@NotNull
	public JsonObject getData() {
		return data;
	}

	/**
	 * @param name  The name of the state
	 * @param value The value
	 * @return {@code this}
	 */
	@NotNull
	public <T> State<M> setState(@NotNull String name, @Nullable T value) {
		return setState(name, old -> value);
	}

	/**
	 * @param name  The name of this state
	 * @param value A function to calculate the new state from the current state
	 * @return {@code this}
	 */
	@NotNull
	public abstract <T> State<M> setState(@NotNull String name, @NotNull Function<T, T> value);

	/**
	 * @param states A {@link Map} of states to set
	 * @return {@code this}
	 */
	@NotNull
	public State<M> putStates(@NotNull Map<String, ?> states) {
		Checks.notNull(states, "states");
		states.forEach(this::setState);
		return this;
	}

	@NotNull
	public Map<String, JsonElement> asMap() {
		return data.asMap();
	}
}
