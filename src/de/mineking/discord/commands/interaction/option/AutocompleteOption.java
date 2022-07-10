package de.mineking.discord.commands.interaction.option;

import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

import de.mineking.discord.commands.interaction.context.AutocompleteContext;

public abstract class AutocompleteOption extends Option {
	public AutocompleteOption(OptionType type, String name, String defaultDescription, boolean isRequired) {
		super(type, name, defaultDescription, isRequired);
		
		super.setAutoComplete(true);
	}
	
	public AutocompleteOption(OptionType type, String name, String defaultDescription) {
		this(type, name, defaultDescription, false);
	}
	
	public AutocompleteOption(OptionType type, String name, boolean isRequired) {
		this(type, name, " ", isRequired);
	}
	
	public AutocompleteOption(OptionType type, String name) {
		this(type, name, false);
	}
	
	public abstract List<Choice> handle(AutocompleteContext context);
}
