package de.mineking.discord.commands;

import de.mineking.discord.commands.annotated.ApplicationCommand;
import net.dv8tion.jda.api.interactions.commands.Command;

public class CommandInfo {
	public final String name;
	public final String description;
	public final String feature;
	public final Command.Type type;
	public final boolean guildOnly;
	public final boolean defer;

	public CommandInfo(String name, String description, String feature, Command.Type type, boolean guildOnly, boolean defer) {
		this.name = name;
		this.description = description;
		this.feature = feature;
		this.type = type;
		this.guildOnly = guildOnly;
		this.defer = defer;
	}

	public static CommandInfo ofAnnotation(ApplicationCommand command) {
		return new CommandInfo(command.name(), command.description(), command.feature(), command.type(), command.guildOnly(), command.defer());
	}
}
