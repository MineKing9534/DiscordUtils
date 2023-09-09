package de.mineking.discord.ui.components.button;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

public class ButtonLabel {
	private final String text;
	private final Emoji emoji;

	public ButtonLabel(@NotNull String text) {
		this.text = text;
		this.emoji = null;
	}

	public ButtonLabel(@NotNull Emoji emoji) {
		this.text = null;
		this.emoji = emoji;
	}

	public Button build(ButtonStyle style, String id) {
		return text != null
				? Button.of(style, id, text)
				: Button.of(style, id, emoji);
	}
}
