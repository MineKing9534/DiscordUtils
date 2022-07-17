package de.mineking.discord.commands.localization;

import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.interaction.Command;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.interaction.context.AutocompleteContext;
import de.mineking.discord.commands.interaction.option.Choice;
import de.mineking.discord.commands.interaction.option.Option;

public interface LocalizationMapper {
	public static class LocalizationResult {
		public final String name;
		public final String description;
		
		private LocalizationResult(String name, String description) {
			this.name = name;
			this.description = description;
		}
		
		@Nonnull
		public static LocalizationResult name(@Nonnull String name) {
			Checks.notNull(name, "name");
			
			return new LocalizationResult(name, null);
		}

		@Nonnull
		public static LocalizationResult description(@Nonnull String description) {
			Checks.notNull(description, "description");
			
			return new LocalizationResult(null, description);
		}
		
		@Nonnull
		public static LocalizationResult create(@Nonnull String name, @Nonnull String description) {
			Checks.notNull(name, "name");
			Checks.notNull(description, "description");
			
			return new LocalizationResult(name, description);
		}
		
		@Nonnull
		public static LocalizationResult createUnsafe(@Nullable String name, @Nullable String description) {
			return new LocalizationResult(name, description);
		}
		
		@Nonnull
		public static LocalizationResult createEmpty() {
			return createUnsafe(null, null);
		}
	}
	
	public List<DiscordLocale> getSupportedLocales();
	public DiscordLocale getDefaultLocale();
	
	public LocalizationResult mapCommand(DiscordLocale locale, Command<?, ?> cmd);
	public LocalizationResult mapOption(DiscordLocale locale, Option o, SlashCommand cmd);
	
	public default LocalizationResult mapChoice(DiscordLocale locale, Choice c, AutocompleteContext context) {
		return LocalizationResult.createEmpty();
	}
	
	public default LocalizationResult mapChoice(DiscordLocale locale, Choice c, Option o, SlashCommand cmd) {
		return LocalizationResult.createEmpty();
	}
}
