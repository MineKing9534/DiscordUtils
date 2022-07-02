package de.mineking.discord.commands.interaction.option;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.interaction.SlashCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class Option extends OptionData {
	public Option(OptionType type, String name, String defaultDescription, boolean isRequired) {
		super(type, name, defaultDescription, isRequired);
	}
	
	public Option(OptionType type, String name, String defaultDescription) {
		this(type, name, defaultDescription, false);
	}
	
	public Option(OptionType type, String name, boolean isRequired) {
		this(type, name, " ", isRequired);
	}

	public Option(OptionType type, String name) {
		this(type, name, false);
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
	public Option addChoices(Choice... choices) {
		super.addChoices(choices);
		
		return this;
	}
	
	@Override
	public Option addChoices(Collection<? extends Choice> choices) {
		super.addChoices(choices);
		
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
	public Option setDescription(String description, Locale... locales) {
		super.setDescription(description, locales);
		
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
	public Option setName(String name, Locale... locales) {
		super.setName(name, locales);
		
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
	 * 		The Command of this option
	 * 
	 * @return The path to get this commands description
	 */
	@Nonnull
	public String getDescriptionPath(SlashCommand cmd) {
		return getDescription() != " " ?
				getDescription() :
				cmd.getPath() + "." + getName();
	}
	
	public OptionData build(SlashCommand cmd) {
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
		
		if(cmd.getFeature().getManager().getLocalizationMapper() != null) {
			{
				Map<Locale, String> locales = cmd.getFeature().getManager().getLocalizationMapper().apply(getDescriptionPath(cmd));
				
				for(var e : locales.entrySet()) {
					data.setDescription(e.getValue(), e.getKey());
				}
				
				if(cmd.getFeature().getManager().getDefaultLanguage() != null) {
					data.setDescription(locales.get(cmd.getFeature().getManager().getDefaultLanguage()));
				}
			}
			
			List<Choice> choices = data.getChoices();
			
			for(Choice c : choices) {
				Map<Locale, String> locales = cmd.getFeature().getManager().getLocalizationMapper().apply(getDescriptionPath(cmd) + "." + c.getName());
				
				for(var e : locales.entrySet()) {
					c.setName(e.getValue(), e.getKey());
				}
				
				if(cmd.getFeature().getManager().getDefaultLanguage() != null) {
					c.setName(locales.get(cmd.getFeature().getManager().getDefaultLanguage()));
				}
			}
			
			try {
				Field f = data.getClass().getDeclaredField("choices");
				
				f.setAccessible(true);
				f.set(data, choices);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
		return data;
	}
}
