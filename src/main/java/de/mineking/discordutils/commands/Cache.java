package de.mineking.discordutils.commands;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cache {
	private final Map<String, Object> data = new HashMap<>();

	public Cache(@NotNull Map<String, ?> data) {
		this.data.putAll(data);
	}

	public Cache() {
		this(Collections.emptyMap());
	}

	@NotNull
	public Map<String, Object> asMap() {
		return data;
	}

	@NotNull
	public Cache putAll(@NotNull Map<String, ?> data) {
		Checks.notNull(data, "data");
		this.data.putAll(data);
		return this;
	}

	@NotNull
	public <T> Cache put(@NotNull String name, @Nullable T value) {
		Checks.notNull(name, "name");
		data.put(name, value);
		return this;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T> T getNullable(@NotNull String name) {
		Checks.notNull(name, "name");
		return (T) data.get(name);
	}

	@NotNull
	public <T> Optional<T> getOptional(@NotNull String name) {
		return Optional.ofNullable(this.getNullable(name));
	}

	@NotNull
	public <T> T getState(@NotNull String name) {
		return this.<T>getOptional(name).orElseThrow();
	}

}
