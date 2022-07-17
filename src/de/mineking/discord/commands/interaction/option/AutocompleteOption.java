package de.mineking.discord.commands.interaction.option;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.interaction.context.AutocompleteContext;
import de.mineking.discord.commands.localization.LocalizationInfo;

public abstract class AutocompleteOption extends Option {
	public AutocompleteOption(@Nonnull OptionType type, @Nonnull String name, @Nullable LocalizationInfo localization, boolean isRequired) {
		super(type, name, localization, isRequired);
		
		super.setAutoComplete(true);
	}
	
	public AutocompleteOption(@Nonnull OptionType type, @Nonnull String name, @Nullable LocalizationInfo localization) {
		this(type, name, localization, false);
	}
	
	public AutocompleteOption(@Nonnull OptionType type, @Nonnull String name, boolean isRequired) {
		this(type, name, null, isRequired);
	}
	
	public AutocompleteOption(@Nonnull OptionType type, @Nonnull String name) {
		this(type, name, false);
	}
	
	public abstract List<Choice> handle(AutocompleteContext context);
}
