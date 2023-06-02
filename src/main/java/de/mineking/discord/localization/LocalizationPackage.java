package de.mineking.discord.localization;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Collections;
import java.util.Map;

public class LocalizationPackage {
	public final String defaultValue;
	public final Map<DiscordLocale, String> values;

	LocalizationPackage(String defaultValue, Map<DiscordLocale, String> values) {
		this.defaultValue = defaultValue;
		this.values = values;
	}

	public static LocalizationPackage constant(String constant) {
		return new LocalizationPackage(constant, Collections.emptyMap());
	}

	public String getValue(DiscordLocale locale) {
		return values.getOrDefault(locale, defaultValue);
	}
}
