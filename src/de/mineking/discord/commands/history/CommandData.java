package de.mineking.discord.commands.history;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;

public class CommandData {
	public final String path;
	public final String name;

	public CommandData(@Nonnull CommandData data) {
		Checks.notNull(data, "data");
		
		this.name = data.name;
		this.path = data.path;
	}
	
	public CommandData(@Nonnull GenericCommandInteractionEvent event) {
		Checks.notNull(event, "event");
		
		this.name = event.getName();
		this.path = event.getCommandPath();
	}
}
