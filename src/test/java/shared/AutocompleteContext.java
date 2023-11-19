package shared;

import de.mineking.discordutils.commands.context.ContextBase;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class AutocompleteContext extends ContextBase<CommandAutoCompleteInteractionEvent> {
	public AutocompleteContext(@NotNull CommandAutoCompleteInteractionEvent event) {
		super(event);
	}
}
