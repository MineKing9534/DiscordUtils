package de.mineking.discordutils.ui.components.button;

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

public enum ButtonColor {
	GREEN(ButtonStyle.SUCCESS), RED(ButtonStyle.DANGER), GRAY(ButtonStyle.SECONDARY), BLUE(ButtonStyle.PRIMARY);

	private final ButtonStyle style;

	ButtonColor(ButtonStyle style) {
		this.style = style;
	}

	/**
	 * @return The {@link ButtonStyle} represented by this {@link ButtonColor}
	 */
	@NotNull
	public ButtonStyle getStyle() {
		return style;
	}
}
