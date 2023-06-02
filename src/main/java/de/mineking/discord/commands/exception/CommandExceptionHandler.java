package de.mineking.discord.commands.exception;

import de.mineking.discord.commands.CommandImplementation;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public interface CommandExceptionHandler {
	void handleException(CommandImplementation command, Exception error, GenericCommandInteractionEvent event);
}
