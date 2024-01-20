package de.mineking.discordutils.localization;

import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record LocalizationManager(@NotNull DiscordLocale defaultLocale, @NotNull Set<DiscordLocale> locales,
								  @NotNull LocalizationFunction function) {
}
