package de.mineking.discord.commands.interaction.option;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.interaction.CommandDataImpl;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.interaction.context.AutocompleteContext;
import de.mineking.discord.commands.localization.LocalizationInfo;

public class Choice extends net.dv8tion.jda.api.interactions.commands.Command.Choice {
	private LocalizationInfo localization;
	
	public Choice(@Nonnull String name, @Nonnull String value, @Nullable LocalizationInfo localization) {
		super(name, value);
		
		this.localization = localization == null ? LocalizationInfo.createEmpty() : localization;
	}
	
	public Choice(@Nonnull String name, @Nonnull String value) {
		this(name, value, null);
	}
	
	public LocalizationInfo getLocalization() {
		return localization;
	}
	
	public net.dv8tion.jda.api.interactions.commands.Command.Choice build(Option option, SlashCommand cmd) {
		
		
		return new net.dv8tion.jda.api.interactions.commands.Command.Choice(getName(), getAsString())
				.setNameLocalizations(
						CommandDataImpl.handle(
								cmd.getFeature().getManager(), 
								(mapper, l) -> mapper.mapChoice(l, this, option, cmd)
						).name
				);
	}
	
	public net.dv8tion.jda.api.interactions.commands.Command.Choice build(AutocompleteContext context) {
		return new net.dv8tion.jda.api.interactions.commands.Command.Choice(getName(), getAsString())
				.setNameLocalizations(
						CommandDataImpl.handle(
								context.cmdMan, 
								(mapper, l) -> mapper.mapChoice(l, this, context)
						).name
				);
	}
}
