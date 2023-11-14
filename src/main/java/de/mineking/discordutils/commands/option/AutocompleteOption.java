package de.mineking.discordutils.commands.option;

import de.mineking.discordutils.commands.context.ContextBase;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link OptionData} implementation that supports autocomplete
 */
public abstract class AutocompleteOption<A extends ContextBase<CommandAutoCompleteInteractionEvent>> extends OptionData {
	/**
	 * Creates a new {@link AutocompleteOption}
	 *
	 * @param type        The {@link OptionType}
	 * @param name        the name of this option
	 * @param description The description of this option
	 * @see OptionData
	 */
	public AutocompleteOption(@NotNull OptionType type, @NotNull String name, @NotNull String description) {
		super(type, name, description);
		setAutoComplete(true);
	}

	/**
	 * Creates a new {@link AutocompleteOption}
	 *
	 * @param type        The {@link OptionType}
	 * @param name        The name of this option
	 * @param description The description of this option
	 * @param isRequired  Whether this option is required
	 * @see OptionData
	 */
	public AutocompleteOption(@NotNull OptionType type, @NotNull String name, @NotNull String description, boolean isRequired) {
		super(type, name, description, isRequired);
		setAutoComplete(true);
	}

	/**
	 * Creates a copy of another option with autocomplete
	 *
	 * @param option The {@link OptionData} to copy
	 */
	public AutocompleteOption(@NotNull OptionData option) {
		this(option.getType(), option.getName(), option.getDescription(), option.isRequired());

		setNameLocalizations(option.getNameLocalizations().toMap());
		setDescriptionLocalizations(option.getDescriptionLocalizations().toMap());

		if(option.getType() == OptionType.CHANNEL) setChannelTypes(option.getChannelTypes());

		else if(option.getType() == OptionType.STRING) {
			if(option.getMinLength() != null) setMinLength(option.getMinLength());
			if(option.getMaxLength() != null) setMaxLength(option.getMaxLength());
		} else if(option.getType() == OptionType.NUMBER) {
			if(option.getMinValue() != null) setMinValue((double) option.getMinValue());
			if(option.getMaxValue() != null) setMaxValue((double) option.getMaxValue());
		} else if(option.getType() == OptionType.INTEGER) {
			if(option.getMinValue() != null) setMinValue((int) option.getMinValue());
			if(option.getMaxValue() != null) setMaxValue((int) option.getMaxValue());
		}
	}

	/**
	 * This es executed every time users interact with this option
	 *
	 * @param context The context holding information about the autocomplete
	 */
	public abstract void handleAutocomplete(@NotNull A context);
}
