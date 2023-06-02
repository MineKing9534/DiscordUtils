package de.mineking.discord.ui.components.button;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public class FrameButton extends ButtonComponent {
	public FrameButton(ButtonColor color, String label, String menu) {
		super(menu, color, label);

		init(menu);
	}

	public FrameButton(ButtonColor color, Emoji label, String menu) {
		super(menu, color, label);

		init(menu);
	}

	private void init(String menu) {
		handler = (m, event) -> m.display(menu);
	}
}
