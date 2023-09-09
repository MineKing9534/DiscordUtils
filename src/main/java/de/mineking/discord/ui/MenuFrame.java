package de.mineking.discord.ui;

public abstract class MenuFrame {
	public final Menu menu;

	public MenuFrame(Menu menu) {
		this.menu = menu;
	}

	public abstract void render();
	public abstract void cleanup();
}
