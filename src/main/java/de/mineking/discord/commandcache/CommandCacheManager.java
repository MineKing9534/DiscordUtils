package de.mineking.discord.commandcache;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import de.mineking.discord.commands.CommandFilter;
import de.mineking.discord.commands.CommandManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Module to handle command updates. This will keep track of your commands' ids to be able to access them when your bot want's to mention them.
 *
 * @see CommandManager
 * @see DiscordUtils
 */
public class CommandCacheManager extends Module {
	private final Map<String, Long> globalCommands = new HashMap<>();
	private final Map<Long, Map<String, Long>> guildCommands = new HashMap<>();

	public CommandCacheManager(@NotNull DiscordUtils manager) {
		super(manager);
	}

	/**
	 * @param name The nme of the command
	 * @return The id of the command
	 */
	public Long getGlobalCommand(@NotNull String name) {
		Checks.notNull(name, "name");
		return globalCommands.getOrDefault(name, 0L);
	}

	/**
	 * @param guild The id of the guild
	 * @param name  The name of the command
	 * @return The id of the command
	 */
	@NotNull
	public Long getGuildCommand(long guild, @NotNull String name) {
		Checks.notNull(name, "name");
		return guildCommands.getOrDefault(guild, Collections.emptyMap()).getOrDefault(name, 0L);
	}

	/**
	 * @return A map of all global command names and ids
	 */
	@NotNull
	public Map<String, Long> getGlobalCommands() {
		return Collections.unmodifiableMap(globalCommands);
	}

	/**
	 * @param guild The id of the guild
	 * @return A map of all guild command names and ids of the specified guild
	 */
	@NotNull
	public Map<String, Long> getGuildCommands(long guild) {
		return Collections.unmodifiableMap(guildCommands.getOrDefault(guild, Collections.emptyMap()));
	}

	/**
	 * Updates all global commands
	 *
	 * @param error An error consumer
	 */
	public void updateGlobalCommands(@Nullable Consumer<Throwable> error) {
		var commandManager = manager.getCommandManager();

		manager.getJDA().updateCommands()
				.addCommands(
						commandManager.findCommands(CommandFilter.TOP.and(CommandFilter.GLOBAL)).stream()
								.map(c -> c.build(commandManager))
								.toList()
				)
				.queue(
						result -> result.forEach(c -> globalCommands.put(c.getName(), c.getIdLong())),
						error
				);
	}

	/**
	 * Updates all guild commands on a specific guild
	 *
	 * @param guild    The guild to update the commands on
	 * @param features A map of all features and their states
	 * @param error    An error consumer
	 */
	public void updateGuildCommands(@NotNull Guild guild, @NotNull Map<String, Boolean> features, @Nullable Consumer<Throwable> error) {
		Checks.notNull(guild, "guild");
		Checks.notNull(features, "features");

		var commandManager = manager.getCommandManager();

		guild.updateCommands()
				.addCommands(
						commandManager.findCommands(CommandFilter.TOP.and(CommandFilter.FEATURE)).stream()
								.filter(c -> features.getOrDefault(c.info.feature, false))
								.filter(c -> c.addToGuild(guild))
								.map(c -> c.build(commandManager))
								.toList()
				)
				.queue(
						result -> {
							var map = guildCommands.getOrDefault(guild.getIdLong(), new HashMap<>());

							result.forEach(c -> map.put(c.getName(), c.getIdLong()));

							guildCommands.put(guild.getIdLong(), map);
						},
						error
				);
	}
}
