package de.mineking.discord.commands.exception;

import de.mineking.discord.commands.CommandImplementation;

public class CommandExecutionException extends RuntimeException {
	public CommandExecutionException(CommandImplementation command, Throwable cause) {
		super("An exception occurred while executing command '" + command.getPath() + "'", cause);
	}
}
