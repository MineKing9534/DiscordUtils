package shared;

import de.mineking.discordutils.commands.context.IAutocompleteContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class AutocompleteContext implements IAutocompleteContext {
	private final CommandAutoCompleteInteractionEvent event;

	public AutocompleteContext(@NotNull CommandAutoCompleteInteractionEvent event) {
		this.event = event;
	}

	@NotNull
	@Override
	public CommandAutoCompleteInteractionEvent getEvent() {
		return event;
	}
}
