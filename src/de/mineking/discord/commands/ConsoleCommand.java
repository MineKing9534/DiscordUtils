package de.mineking.discord.commands;

public interface ConsoleCommand {
	/**
	 * @param command
	 * 		The name of the command
	 * 
	 * @param args
	 * 		The arguments for the execution; args[0] = commands
	 */
	public void performCommand(String command, String[] args);
}