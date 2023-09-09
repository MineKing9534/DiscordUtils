package de.mineking.discord.ui.components.button;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public class FrameButton extends ButtonComponent {
	public FrameButton(ButtonColor color, String label, String menu) {
		super(menu, color, label);
		addHandler((m, event) -> m.display(menu));
	}

	public FrameButton(ButtonColor color, Emoji label, String menu) {
		super(menu, color, label);
		addHandler((m, event) -> m.display(menu));
	}
}
