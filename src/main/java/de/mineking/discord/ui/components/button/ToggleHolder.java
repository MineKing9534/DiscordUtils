package de.mineking.discord.ui.components.button;

public interface ToggleHolder {
	void setState(boolean state);

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
		public void setState(boolean state) {
			this.state = state;
		}
	}
}
