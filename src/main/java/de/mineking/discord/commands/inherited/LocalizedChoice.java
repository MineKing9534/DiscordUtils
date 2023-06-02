package de.mineking.discord.commands.inherited;

import de.mineking.discord.commands.Choice;
import de.mineking.discord.localization.LocalizationManager;
import net.dv8tion.jda.api.interactions.commands.Command;

public class LocalizedChoice extends Choice {
	public final boolean custom;

	public LocalizedChoice(String name, String value, boolean custom) {
		super(name, value);

		this.custom = custom;
	}

	public static LocalizedChoice localize(String name, String value) {
		return new LocalizedChoice(name, value, false);
	}

	public static LocalizedChoice withPath(String path, String value) {
		return new LocalizedChoice(path, value, true);
	}

	@Override
	public Command.Choice build(String command, String option, LocalizationManager manager) {
		var localization = manager.getChoiceDescription(command, option, this);

		return new Command.Choice(localization.defaultValue, value)
				.setNameLocalizations(localization.values);
	}
}
