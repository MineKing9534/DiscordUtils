package de.mineking.discord.commands.reply;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.history.RuntimeData;
import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.RestAction;

public class ReplyManager {	
	private boolean replied;
	
	private RuntimeData execData;
	
	private GenericCommandInteractionEvent event;
	private MessageChannel channel;

	/**
	 * @param data
	 * 		The ExecutionData of your Command
	 */
	public ReplyManager(@Nonnull RuntimeData data) {
		Checks.nonNull(data, "data");
		
		this.execData = data;
		
		this.replied = false;
		
		this.channel = data.getChannel();
		this.event = data.getEvent();
	}
	
	/**
	 * Replies to the command
	 * <b>WARNING:</b> This does only work for acknowledged interactions. To use this enable the defaultAcknowledge field or acknowledge the interaction yourself!
	 * 
	 * @param mes
	 * 		The Message to reply
	 * 
	 * @return A MessageReplyAction for handling the sending
	 */
	@Nonnull
	public RestAction<Message> reply(@Nonnull Message mes) {
		Checks.nonNull(mes, "mes");
		
		RestAction<Message> action;
		
		if(event != null && !replied) {
			action = event.getHook().editOriginal(mes);
		}
		
		else if(channel != null) {
			action = channel.sendMessage(mes);
		}
		
		else {
			throw new IllegalStateException();
		}
		
		replied = true;
		
		return action;	
	}
	
	/**
	 * Replies a modal to the command
	 * 
	 * @param modal
	 * 		The modal to reply
	 * 
	 * @return A ModalReplyAction for handling the sending
	 */
	public ModalReplyAction reply(@Nonnull Modal modal) {
		Checks.nonNull(modal, "modal");
		
		if(event != null) {
			replied = true;
			
			modal = modal.createCopy()
					.setId(UUID.randomUUID() + ":" + modal.getId())
					.build();
			
			return new ModalReplyAction(this, modal, event.replyModal(modal));
		}
		
		else {
			throw new IllegalStateException("You can only reply a modal to commands directly from discord");
		}
	}
	
	/**
	 * @return The ExecutionData of this Command
	 */
	public RuntimeData getExecutionData() {
		return execData;
	}
	
	/**
	 * @return Whether there already was a reply
	 */
	public boolean isReplied() {
		return replied;
	}
	
	/**
	 * @return The GenericCommandInteractionEvent or null
	 */
	@Nullable
	public GenericCommandInteractionEvent getEvent() {
		return event;
	}
	
	/**
	 * @return The MessageChannel or null
	 */
	@Nullable
	public MessageChannel getChannel() {
		return channel;
	}
}
