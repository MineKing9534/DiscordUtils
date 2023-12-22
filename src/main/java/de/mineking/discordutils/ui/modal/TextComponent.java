package de.mineking.discordutils.ui.modal;

import de.mineking.discordutils.ui.state.DataState;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TextComponent {
	public final String name;

	private final Function<DataState<ModalMenu>, String> label;
	private final TextInputStyle style;

	private Function<DataState<ModalMenu>, String> value = state -> null;
	private Function<DataState<ModalMenu>, String> placeholder = state -> null;

	private Function<DataState<ModalMenu>, Integer> minLength = state -> null;
	private Function<DataState<ModalMenu>, Integer> maxLength = state -> null;

	private Function<DataState<ModalMenu>, Boolean> required = state -> true;

	/**
	 * @param name  The name of this component
	 * @param label A function to provide the label for this component
	 * @param style The {@link TextInputStyle}
	 */
	public TextComponent(@NotNull String name, @NotNull Function<DataState<ModalMenu>, String> label, @NotNull TextInputStyle style) {
		Checks.notNull(name, "name");
		Checks.notNull(label, "label");
		Checks.notNull(style, "style");

		this.name = name;
		this.label = label;
		this.style = style;
	}

	/**
	 * @param name  The name of this component
	 * @param label The label for this component
	 * @param style The {@link TextInputStyle}
	 */
	public TextComponent(@NotNull String name, @NotNull String label, @NotNull TextInputStyle style) {
		this(name, x -> label, style);
	}

	/**
	 * @param value A function to provide a default value
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setValue(@NotNull Function<DataState<ModalMenu>, String> value) {
		Checks.notNull(value, "value");
		this.value = value;
		return this;
	}

	/**
	 * @param value The default value
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setValue(@Nullable String value) {
		return setValue(state -> value);
	}

	/**
	 * @param placeholder A function to provide the placeholder
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setPlaceholder(@NotNull Function<DataState<ModalMenu>, String> placeholder) {
		Checks.notNull(placeholder, "placeholder");
		this.placeholder = placeholder;
		return this;
	}

	/**
	 * @param placeholder The placeholder
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setPlaceholder(@Nullable String placeholder) {
		return setPlaceholder(state -> placeholder);
	}

	/**
	 * @param minLength A function to provide the minimum required length of the input
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setMinLength(@NotNull Function<DataState<ModalMenu>, Integer> minLength) {
		Checks.notNull(minLength, "minLength");
		this.minLength = minLength;
		return this;
	}

	/**
	 * @param minLength The minimum required length of the input
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setMinLength(@Nullable Integer minLength) {
		return setMinLength(state -> minLength);
	}

	/**
	 * @param maxLength A function to provide the maximum allowed length of the input
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setMaxLength(@NotNull Function<DataState<ModalMenu>, Integer> maxLength) {
		Checks.notNull(maxLength, "maxLength");
		this.maxLength = maxLength;
		return this;
	}

	/**
	 * @param maxLength The maximum allowed length of the input
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setMaxLength(@Nullable Integer maxLength) {
		return setMaxLength(state -> maxLength);
	}

	/**
	 * @param required A function to specify whether this component is required
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setRequired(@NotNull Function<DataState<ModalMenu>, Boolean> required) {
		Checks.notNull(required, "required");
		this.required = required;
		return this;
	}

	/**
	 * @param required Whether this component is required
	 * @return {@code this}
	 */
	@NotNull
	public TextComponent setValue(boolean required) {
		return setRequired(state -> required);
	}

	/**
	 * @param id    The id to use
	 * @param state The current {@link DataState}
	 * @return The resulting {@link TextInput}
	 */
	@NotNull
	public TextInput build(@NotNull String id, @NotNull DataState<ModalMenu> state) {
		var temp = TextInput.create(id, label.apply(state), style)
				.setValue(value.apply(state))
				.setPlaceholder(placeholder.apply(state))
				.setRequired(required.apply(state));

		var minLength = this.minLength.apply(state);
		var maxLength = this.maxLength.apply(state);

		if(minLength != null) temp.setMinLength(minLength);
		if(maxLength != null) temp.setMaxLength(maxLength);

		return temp.build();
	}
}
