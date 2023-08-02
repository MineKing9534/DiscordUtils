package de.mineking.discord.ui.components.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ToggleHolder {
	void setState(boolean state, ButtonInteractionEvent event);

	boolean getState();

	class DefaultToggleHolder implements ToggleHolder {
		private boolean state;

		public DefaultToggleHolder(boolean start) {
			this.state = start;
		}

		@Override
		public boolean getState() {
			return state;
		}

		@Override
		public void setState(boolean state, ButtonInteractionEvent event) {
			this.state = state;
		}
	}
}
