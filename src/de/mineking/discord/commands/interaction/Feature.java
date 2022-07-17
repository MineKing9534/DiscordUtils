package de.mineking.discord.commands.interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.internal.utils.Checks;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.interaction.context.CommandContext;

public class Feature {
	private CommandManager cmdMan;

	private String name;
	private Map<String, Command<?, ?>> commands;

	public Feature(@Nonnull String name, @Nonnull CommandManager cmdMan) {
		Checks.notNull(name, "name");
		Checks.notNull(cmdMan, "cmdMan");
		
		this.cmdMan = cmdMan;
		this.name = name;
		
		commands = new LinkedHashMap<>();
	}
	
	/**
	 * @return The CommandManager this features is attached to
	 */
	public CommandManager getManager() {		
		return cmdMan;
	}
	
	/**
	 * @return The current name of this feature
	 */
	@Nonnull
	public String getName() {
		return name;
	}
	
	/**
	 * Adds a new command to this feature
	 * 
	 * @param command
	 * 		The name of the command
	 * 
	 * @param cmd
	 * 		A Command
	 * 
	 * @return The same Feature instance
	 */
	public <T extends GenericCommandInteractionEvent, C extends CommandContext<T>> Feature addCommand(@Nonnull String command, @Nonnull Command<T, C> cmd) {	
		Checks.notNull(command, "command");
		Checks.notNull(cmd, "cmd");
		
		try {
			Command<T, C> c = cmd.createClone();
			
			c.name = command;
			c.feature = this;
			
			if(c instanceof SlashCommand sc) {
				updateSubcommands(sc);
			}
			
			commands.put(command, c);

			return this;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void updateSubcommands(SlashCommand cmd) {
		for(SlashCommand c : cmd.getSubcommandsRaw().values()) {
			c.feature = cmd.getFeature();
			
			updateSubcommands(c);
		}
	}
	
	/**
	 * @param guild
	 * 		The Guild
	 * 
	 * @return Whether the specified Feature is enabled for the provided Guild
	 */
	public boolean isEnabled(@Nonnull Guild guild) {
		Checks.notNull(guild, "guild");
		
		return name.equals("std") ? true :
			cmdMan.getFeatureStateGetter().test(guild, this);
	}
	
	/**
	 * @return All commands added to this feature
	 */
	@Nonnull
	public Map<String, Command<?, ?>> getCommands() {
		return commands;
	}
}
