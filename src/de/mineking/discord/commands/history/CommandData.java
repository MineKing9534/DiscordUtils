package de.mineking.discord.commands.history;

import javax.annotation.Nonnull;

import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public class CommandData {
	public final String path;
	public final String name;

	public CommandData(@Nonnull CommandData data) {
		Checks.nonNull(data, "data");
		
		this.name = data.name;
		this.path = data.path;
	}
	
	public CommandData(@Nonnull GenericCommandInteractionEvent event) {
		Checks.nonNull(event, "event");
		
		this.name = event.getName();
		this.path = event.getCommandPath();
	}
}
