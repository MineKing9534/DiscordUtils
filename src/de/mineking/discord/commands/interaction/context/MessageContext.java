package de.mineking.discord.commands.interaction.context;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.history.ExecutionData;

public class MessageContext extends CommandContext<MessageContextInteractionEvent> {
	public final Message target;
	
	public MessageContext(CommandManager cmdMan, ExecutionData<MessageContextInteractionEvent, ? extends CommandContext<MessageContextInteractionEvent>> data) {
		super(cmdMan, data);
		
		target = event.getTarget();
	}
}
