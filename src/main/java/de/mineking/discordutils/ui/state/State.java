package de.mineking.discordutils.ui.state;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.mineking.discordutils.ui.Menu;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class State {
	public final static Gson gson = new Gson();

	@NotNull
	public final Menu menu;

	@NotNull
	public final JsonObject data;

	State(@NotNull Menu menu, @NotNull JsonObject data) {
		Checks.notNull(menu, "menu");
		Checks.notNull(data, "data");

		this.menu = menu;
		this.data = data;
	}

	/**
	 * @param name  The name of the state
	 * @param value The value
	 * @return {@code this}
	 */
	@NotNull
	public <T> State setState(@NotNull String name, @Nullable T value) {
		return setState(name, old -> value);
	}

	/**
	 * @param name  The name of this state
	 * @param value A function to calculate the new state from the current state
	 * @return {@code this}
	 */
	@NotNull
	public abstract <T> State setState(@NotNull String name, @NotNull Function<T, T> value);
}
