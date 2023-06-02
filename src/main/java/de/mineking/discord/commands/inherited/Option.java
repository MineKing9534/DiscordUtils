package de.mineking.discord.commands.inherited;

import de.mineking.discord.commands.Choice;
import de.mineking.discord.localization.LocalizationManager;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public class Option {
	public final OptionType type;
	public final String name;
	public final String description;

	public boolean localize = false;

	protected boolean required;
	protected boolean autocomplete;

	protected final EnumSet<ChannelType> channelTypes = EnumSet.noneOf(ChannelType.class);

	protected Number minValue;
	protected Number maxValue;
	protected Integer minLength;
	protected Integer maxLength;

	protected final List<Choice> choices = new LinkedList<>();

	public Option(OptionType type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	public Option(OptionType type, String name) {
		this(type, name, "");
	}

	public Option localizeCustom() {
		this.localize = true;

		return this;
	}

	public Option required() {
		this.required = true;

		return this;
	}

	public Option choices(List<Choice> choices) {
		this.choices.addAll(choices);

		return this;
	}

	public Option channelTypes(ChannelType... types) {
		channelTypes.addAll(Arrays.asList(types));

		return this;
	}

	public Option range(Number minValue, Number maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;

		return this;
	}

	public Option length(Integer minValue, Integer maxValue) {
		this.minLength = minValue;
		this.maxLength = maxValue;

		return this;
	}

	public OptionData build(String command, LocalizationManager manager) {
		var localization = manager.getOptionDescription(command, this);

		var option = new OptionData(type, name, localization.defaultValue)
				.setDescriptionLocalizations(localization.values)
				.setAutoComplete(autocomplete)
				.setRequired(required)
				.setAutoComplete(autocomplete);

		if(!channelTypes.isEmpty()) {
			option.setChannelTypes(channelTypes);
		}

		if(!choices.isEmpty()) {
			option.addChoices(choices.stream().map(c -> c.build(command, name, manager)).toList());
		}

		if(this.minLength != null) {
			option.setMinLength(minLength);
		}

		if(this.maxLength != null) {
			option.setMinLength(maxLength);
		}

		if(this.minValue != null) {
			if(minValue instanceof Long || minValue instanceof Integer) {
				option.setMinValue(minValue.longValue());
			}

			else if(minValue instanceof Double) {
				option.setMinValue(minValue.doubleValue());
			}
		}

		if(this.maxValue != null) {
			if(maxValue instanceof Long || maxValue instanceof Integer) {
				option.setMaxValue(maxValue.longValue());
			}

			else if(maxValue instanceof Double) {
				option.setMaxValue(maxValue.doubleValue());
			}
		}

		return option;
	}
}
