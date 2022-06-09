package de.mineking.discord.commands.history;

import javax.annotation.Nonnull;

import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public class CommandData {
	protected String path;
	protected String name;

	public CommandData(@Nonnull CommandData data) {
		Checks.nonNull(data, "data");
		
		this.name = data.getName();
		this.path = data.getPath();
	}
	
	public CommandData(@Nonnull GenericCommandInteractionEvent event) {
		Checks.nonNull(event, "event");
		
		this.name = event.getName();
		this.path = event.getCommandPath();
	}
	
	/**
	 * @return The name of the command
	 */
	@Nonnull
	public String getName() {
		return name;
	}
	
	/**
	 * @return The execution path of the command
	 */
	@Nonnull
	public String getPath() {
		return path;
	}
}
