package shared;

import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class CommandContext implements ICommandContext {
	private final GenericCommandInteractionEvent event;

	public CommandContext(@NotNull GenericCommandInteractionEvent event) {
		this.event = event;
	}

	@NotNull
	@Override
	public GenericCommandInteractionEvent getEvent() {
		return event;
	}
}
