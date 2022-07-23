package de.mineking.discord.commands.interaction.option;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.interaction.context.AutocompleteContext;
import de.mineking.discord.commands.localization.*;
import de.mineking.discord.commands.localization.LocalizationInfo.LocalizationPackage;
import de.mineking.discord.commands.localization.LocalizationMapper.LocalizationResult;

public class Choice extends net.dv8tion.jda.api.interactions.commands.Command.Choice implements Localizable {
	private LocalizationInfo localization;
	
	public Choice(@Nonnull String value, @Nullable LocalizationPackage localization) {
		super(localization != null ? localization.key : value, value);
		
		this.localization = localization == null ? LocalizationInfo.createEmpty() : LocalizationInfo.name(localization);
	}
	
	public Choice(@Nonnull String value) {
		this(value, (LocalizationPackage)null);
	}
	
	public Choice(@Nonnull String name, @Nonnull String value) {
		this(value, LocalizationPackage.constant(name));
	}
	
	@Nullable
	@Override
	public LocalizationInfo getLocalization() {
		return localization;
	}
	
	private net.dv8tion.jda.api.interactions.commands.Command.Choice build(CommandManager cmdMan, BiFunction<LocalizationMapper, DiscordLocale, LocalizationResult> handler) {
		net.dv8tion.jda.api.interactions.commands.Command.Choice data = new net.dv8tion.jda.api.interactions.commands.Command.Choice(getName(), getAsString());
		
		LocalizationHolder holder = LocalizationUtils.handle(
				cmdMan, 
				handler,
				this
		);
		
		data.setNameLocalizations(holder.name);
		
		if(holder.defaultName != null) {
			data.setName(holder.defaultName);
		}
		
		return data;
	}
	
	public net.dv8tion.jda.api.interactions.commands.Command.Choice build(Option option, SlashCommand cmd) {
		return build(
				cmd.getFeature().getManager(),
				(mapper, l) -> mapper.mapChoice(l, this, option, cmd)
		);
	}
	
	public net.dv8tion.jda.api.interactions.commands.Command.Choice build(AutocompleteContext context) {
		return build(
				context.cmdMan,
				(mapper, l) -> mapper.mapChoice(l, this, context)
		);
	}
}
