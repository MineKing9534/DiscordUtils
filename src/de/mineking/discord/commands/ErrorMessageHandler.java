package de.mineking.discord.commands;

import de.mineking.discord.commands.history.RuntimeData;

public interface ErrorMessageHandler {
	public default void unlicensed(RuntimeData data, CommandPermission required) {}
	public default void invalidPage(RuntimeData data, int current, int max) {}
}
