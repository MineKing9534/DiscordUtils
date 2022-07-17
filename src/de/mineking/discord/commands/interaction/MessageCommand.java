package de.mineking.discord.commands.interaction;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import de.mineking.discord.commands.history.ExecutionData;
import de.mineking.discord.commands.interaction.context.MessageContext;

public abstract class MessageCommand extends ContextCommand<MessageContextInteractionEvent, MessageContext> {
	public MessageCommand() {
		super(MessageContextInteractionEvent.class, Command.Type.MESSAGE);
	}
	
	@Override
	protected MessageContext buildContext(ExecutionData<MessageContextInteractionEvent, MessageContext> data) {
		return new MessageContext(getFeature().getManager(), data);
	}
}
