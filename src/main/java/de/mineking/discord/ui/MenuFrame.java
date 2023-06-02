package de.mineking.discord.ui;

public abstract class MenuFrame {
	public final Menu menu;

	public MenuFrame(Menu menu) {
		this.menu = menu;
	}

	public abstract void show();

	public abstract void cleanup();
}
