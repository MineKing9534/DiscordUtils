package de.mineking.discordutils.commands.context;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IAutocompleteContext {
	/**
	 * @return The {@link CommandAutoCompleteInteractionEvent} of this autocomplete interaction
	 */
	@NotNull
	CommandAutoCompleteInteractionEvent getEvent();
}
