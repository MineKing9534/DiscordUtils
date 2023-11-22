package de.mineking.discordutils;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class Manager extends ListenerAdapter {
	DiscordUtils<?> manager;

	@NotNull
	public DiscordUtils<?> getManager() {
		return manager;
	}
}
