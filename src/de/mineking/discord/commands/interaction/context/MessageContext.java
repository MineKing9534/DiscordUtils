package de.mineking.discord.commands.interaction.context;

import de.mineking.discord.commands.history.ExecutionData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public class MessageContext extends CommandContext<MessageContextInteractionEvent> {
	public final Message target;
	
	public MessageContext(ExecutionData<MessageContextInteractionEvent, ? extends CommandContext<MessageContextInteractionEvent>> data) {
		super(data);
		
		target = event.getTarget();
	}
}
