package de.mineking.discordutils.commands.context;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ICommandContext {
	/**
	 * @return The {@link GenericCommandInteractionEvent} for this command
	 */
	@NotNull
	GenericCommandInteractionEvent getEvent();
}
