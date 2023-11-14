package de.mineking.discordutils.localization;

import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record Localization(@NotNull String defaultValue, @NotNull Map<DiscordLocale, String> values) {
}
