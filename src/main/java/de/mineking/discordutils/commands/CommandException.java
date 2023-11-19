package de.mineking.discordutils.commands;

import org.jetbrains.annotations.NotNull;

public class CommandException extends Exception {
	public final Command<?> command;

	public CommandException(@NotNull Command<?> command, @NotNull Throwable cause) {
		super(cause);
		this.command = command;
	}
}
