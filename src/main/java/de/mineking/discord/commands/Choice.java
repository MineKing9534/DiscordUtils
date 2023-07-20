package de.mineking.discord.commands;

import de.mineking.discord.localization.LocalizationManager;
import net.dv8tion.jda.api.interactions.commands.Command;

public class Choice {
	public final String name;
	public final String value;

	public Choice(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Choice(String name) {
		this(name, name);
	}

	public Command.Choice build(String command, String option, LocalizationManager manager) {
		return new Command.Choice(name, value);
	}
}
