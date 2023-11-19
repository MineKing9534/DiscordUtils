package shared;

import de.mineking.discordutils.commands.context.ContextBase;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class CommandContext extends ContextBase<GenericCommandInteractionEvent> {
	public CommandContext(@NotNull GenericCommandInteractionEvent event) {
		super(event);
	}
}
