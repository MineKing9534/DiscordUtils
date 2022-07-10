package de.mineking.discord.commands.interaction;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.history.ExecutionData;
import de.mineking.discord.commands.interaction.context.MessageContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class MessageCommand extends ContextCommand<MessageContextInteractionEvent, MessageContext> {
	public MessageCommand() {
		super(MessageContextInteractionEvent.class);
	}
	
	@Override
	protected MessageContext buildContext(ExecutionData<MessageContextInteractionEvent, MessageContext> data) {
		return new MessageContext(data);
	}
	
	@Override
	@Nonnull
	public final CommandData build(Guild g) {
		CommandData data = Commands.message(getName());
		
		if(getFeature().getManager().getLocalizationMapper() != null) {
			data.setNameLocalizations(getFeature().getManager().getLocalizationMapper().apply(getPath()));
		}
		
		return data;
	}
}
