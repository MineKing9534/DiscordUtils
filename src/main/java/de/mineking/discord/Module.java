package de.mineking.discord;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

/**
 * A class representing a module in the {@link DiscordUtils} manager.
 */
public class Module extends ListenerAdapter {
	protected final DiscordUtils manager;

	/**
	 * Creates an instance of this module.
	 *
	 * @param manager The {@link DiscordUtils} instance that manages this module
	 */
	public Module(@NotNull DiscordUtils manager) {
		Checks.notNull(manager, "manager");
		this.manager = manager;
	}

	/**
	 * @return The {@link DiscordUtils} instance that manages this module
	 */
	public DiscordUtils getManager() {
		return manager;
	}

	/**
	 * Shuts down this module and cleans up
	 */
	public void cleanup() {}
}
