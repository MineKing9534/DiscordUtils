package de.mineking.discordutils.ui.components.button;

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public enum ButtonColor {
	GREEN(ButtonStyle.SUCCESS),
	RED(ButtonStyle.DANGER),
	GRAY(ButtonStyle.SECONDARY),
	BLUE(ButtonStyle.PRIMARY);

	public final ButtonStyle style;

	ButtonColor(ButtonStyle style) {
		this.style = style;
	}
}
