package de.mineking.discordutils.localization;

import de.mineking.discordutils.commands.Command;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public interface LocalizationFunction {
	/**
	 * @param command The {@link Command}
	 * @return The localization path
	 */
	@NotNull
	default String getCommandPath(@NotNull Command<?> command) {
		return command.getPath(".") + ".description";
	}

	/**
	 * @param command The {@link Command}
	 * @param option  The {@link OptionData}
	 * @return The localization path
	 */
	@NotNull
	default String getOptionPath(@NotNull Command<?> command, @NotNull OptionData option) {
		return command.getPath(".") + ".options." + option.getName() + ".description";
	}

	/**
	 * @param command The {@link Command}
	 * @param option  The {@link OptionData}
	 * @param choice  The {@link net.dv8tion.jda.api.interactions.commands.Command.Choice}
	 * @return The localization path
	 */
	@NotNull
	default String getChoicePath(@NotNull Command<?> command, @NotNull OptionData option, @NotNull net.dv8tion.jda.api.interactions.commands.Command.Choice choice) {
		return command.getPath(".") + ".options." + option.getName() + ".choices." + choice.getName();
	}

	/**
	 * @param path   The path to localize
	 * @param locale The {@link DiscordLocale}
	 * @return The resulting string
	 */
	@NotNull
	String localize(@NotNull String path, @NotNull DiscordLocale locale);
}
