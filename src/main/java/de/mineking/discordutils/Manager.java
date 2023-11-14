package de.mineking.discordutils;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

public abstract class Manager extends ListenerAdapter {
	@NotNull
	public final DiscordUtils<?> manager;

	public Manager(@NotNull DiscordUtils<?> manager) {
		Checks.notNull(manager, "manager");

		this.manager = manager;
	}
}
