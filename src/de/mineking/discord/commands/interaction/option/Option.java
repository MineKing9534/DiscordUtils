package de.mineking.discord.commands.interaction.option;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.interaction.CommandDataImpl;
import de.mineking.discord.commands.interaction.CommandDataImpl.LocalizationHolder;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.localization.LocalizationInfo;

public class Option extends OptionData {
	private final LocalizationInfo localization;
	
	private final List<Command.Choice> choices = new ArrayList<>();
	
	public Option(@Nonnull OptionType type, @Nonnull String name, @Nullable LocalizationInfo localization, boolean isRequired) {
		super(type, name, name, isRequired);
		
		this.localization = localization == null ? LocalizationInfo.createEmpty() : localization;
	}
	
	public Option(@Nonnull OptionType type, @Nonnull String name, @Nullable LocalizationInfo localization) {
		this(type, name, localization, false);
	}
	
	public Option(@Nonnull OptionType type, @Nonnull String name, boolean isRequired) {
		this(type, name, null, isRequired);
	}
	
	public Option(@Nonnull OptionType type, @Nonnull String name) {
		this(type, name, false);
	}
	
	public LocalizationInfo getLocalization() {
		return localization;
	}
	
	@Override
	public Option addChoice(String name, double value) {
		super.addChoice(name, value);
		
		return this;
	}
	
	@Override
	public Option addChoice(String name, long value) {
		super.addChoice(name, value);
		
		return this;
	}
	
	@Override
	public Option addChoice(String name, String value) {
		super.addChoice(name, value);
		
		return this;
	}
	
	@Override
	public Option addChoices(Command.Choice... choices) {
		super.addChoices(choices);
		
		return this;
	}
	
	@Override
	public Option addChoices(@Nonnull Collection<? extends Command.Choice> choices) {
		Checks.notNull(choices, "choices");
		
		if(isAutoComplete() || !getType().canSupportChoices()) {
			throw new UnsupportedOperationException();
		}
		
		net.dv8tion.jda.internal.utils.Checks.check(choices.size() + this.choices.size() <= MAX_CHOICES, "Cannot have more than 25 choices for one option!");
		
		this.choices.addAll(choices);
		
		return this;
	}

	@Override
	public Option setAutoComplete(boolean autoComplete) {
		super.setAutoComplete(autoComplete);
		
		return this;
	}
	
	@Override
	public Option setChannelTypes(ChannelType... channelTypes) {
		super.setChannelTypes(channelTypes);
		
		return this;
	}
	
	@Override
	public Option setChannelTypes(Collection<ChannelType> channelTypes) {
		super.setChannelTypes(channelTypes);
		
		return this;
	}
	
	@Override
	public Option setDescription(String description) {
		super.setDescription(description);
		
		return this;
	}
	
	@Override
	public Option setDescriptionLocalization(DiscordLocale locale, String description) {
		super.setDescriptionLocalization(locale, description);
		
		return this;
	}
	
	@Override
	public Option setDescriptionLocalizations(Map<DiscordLocale, String> map) {
		super.setDescriptionLocalizations(map);
		
		return this;
	}
	
	@Override
	public Option setMaxValue(double value) {
		super.setMaxValue(value);
		
		return this;
	}
	
	@Override
	public Option setMaxValue(long value) {
		super.setMaxValue(value);
		
		return this;
	}
	
	@Override
	public Option setMinValue(double value) {
		super.setMinValue(value);
		
		return this;
	}
	
	@Override
	public Option setMinValue(long value) {
		super.setMinValue(value);
		
		return this;
	}
	
	@Override
	public Option setName(String name) {
		super.setName(name);
		
		return this;
	}
	
	@Override
	public Option setNameLocalization(DiscordLocale locale, String name) {
		super.setNameLocalization(locale, name);
		
		return this;
	}
	
	@Override
	public Option setNameLocalizations(Map<DiscordLocale, String> map) {
		super.setNameLocalizations(map);
		
		return this;
	}
	
	@Override
	public Option setRequired(boolean required) {
		super.setRequired(required);
		
		return this;
	}
	
	@Override
	public Option setRequiredRange(double minValue, double maxValue) {
		super.setRequiredRange(minValue, maxValue);
		
		return this;
	}
	
	@Override
	public Option setRequiredRange(long minValue, long maxValue) {
		super.setRequiredRange(minValue, maxValue);
		
		return this;
	}

	/**
	 * @param cmd
	 * 		The command this option should be built for. It is not recommended to use this method on your own!
	 * 
	 * @return The resulting OptionData
	 */
	@Nonnull
	public OptionData build(@Nonnull SlashCommand cmd) {
		Checks.notNull(cmd, "cmd");
		
		OptionData data = new OptionData(getType(), getName(), getDescription(), isRequired(), isAutoComplete());
		
		try {
			for(Field f : OptionData.class.getDeclaredFields()) {
				f.setAccessible(true);
				
				if(!Modifier.isStatic(f.getModifiers())) {
					f.set(data, f.get(this));
				}
			}
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			
			return this;
		}
		
		LocalizationHolder holder = CommandDataImpl.handleOption(cmd, this);
		
		data.setDescriptionLocalizations(holder.description);
		data.setNameLocalizations(holder.name);
		
		return data;
	}
}
