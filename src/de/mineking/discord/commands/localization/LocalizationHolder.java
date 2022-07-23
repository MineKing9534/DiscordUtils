package de.mineking.discord.commands.localization;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.HashMap;
import java.util.Map;

public class LocalizationHolder {
	public final Map<DiscordLocale, String> name;
	public final Map<DiscordLocale, String> description;
	
	public final String defaultName;
	public final String defaultDescription;

	private LocalizationHolder(Map<DiscordLocale, String> name, Map<DiscordLocale, String> description, String defaultName, String defaultDescription) {
		this.name = name;
		this.description = description;
		
		this.defaultName = defaultName;
		this.defaultDescription = defaultDescription;
	}
	
	public LocalizationHolder(Map<DiscordLocale, String> name, Map<DiscordLocale, String> description, DiscordLocale defaultLocale) {
		this(name, description, name.get(defaultLocale), description.get(defaultLocale));
	}
	
	public static LocalizationHolder empty() {
		return defaults(null, null);
	}
	
	public static LocalizationHolder defaults(String defaultName, String defaultDescription) {
		return new LocalizationHolder(new HashMap<>(), new HashMap<>(), defaultName, defaultDescription);
	}
}