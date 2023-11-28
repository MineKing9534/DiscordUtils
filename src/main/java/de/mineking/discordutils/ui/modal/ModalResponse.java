package de.mineking.discordutils.ui.modal;

import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModalResponse {
	private final Map<String, String> values;

	public ModalResponse(List<ModalMapping> values) {
		this.values = values.stream()
				.collect(Collectors.toMap(
						m -> m.getId().split(":")[0],
						ModalMapping::getAsString
				));
	}

	@Nullable
	public String getNullableString(@NotNull String name) {
		Checks.notNull(name, "name");
		return values.get(name);
	}

	@NotNull
	public Optional<String> getOptionalString(@NotNull String name) {
		return Optional.ofNullable(getNullableString(name));
	}

	@NotNull
	public String getString(@NotNull String name) {
		return getOptionalString(name).orElseThrow();
	}

	@Nullable
	public <T> T getNullable(@NotNull String name, @NotNull Function<String, T> mapper) {
		Checks.notNull(mapper, "mapper");
		var value = getNullableString(name);
		return value == null ? null : mapper.apply(value);
	}

	@NotNull
	public <T> Optional<T> getOptional(@NotNull String name, @NotNull Function<String, T> mapper) {
		return Optional.ofNullable(getNullable(name, mapper));
	}

	@NotNull
	public <T> T get(@NotNull String name, @NotNull Function<String, T> mapper) {
		return getOptional(name, mapper).orElseThrow();
	}

	@NotNull
	public Optional<Integer> getOptionalInteger(@NotNull String name) {
		return Optional.ofNullable(getNullableString(name))
				.map(Integer::parseInt);
	}

	@Nullable
	public Integer getNullableInteger(@NotNull String name) {
		return getOptionalInteger(name).orElse(null);
	}

	public int getInteger(@NotNull String name) {
		return getOptionalInteger(name).orElseThrow();
	}

	@NotNull
	public Optional<Double> getOptionalDouble(@NotNull String name) {
		return Optional.ofNullable(getNullableString(name))
				.map(Double::parseDouble);
	}

	@Nullable
	public Double getNullableDouble(@NotNull String name) {
		return getOptionalDouble(name).orElse(null);
	}

	public double getDouble(@NotNull String name) {
		return getOptionalDouble(name).orElseThrow();
	}
}
