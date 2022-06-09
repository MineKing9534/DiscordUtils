package de.mineking.discord.commands.reply;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import de.mineking.discord.commands.interaction.handler.ModalHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;

public class ModalReplyAction implements ModalCallbackAction {
	ReplyManager man;
	
	private ModalCallbackAction action;
	
	private Modal modal;
	
	private ModalHandler handler;
	
	ModalReplyAction(ReplyManager man, Modal modal, ModalCallbackAction action) {
		this.man = man;
		
		this.action = action;
		
		this.modal = modal;
	}
	
	public ModalReplyAction handle(ModalHandler handler) {
		this.handler = handler;
		
		return this;
	}
	
	@Override
	public JDA getJDA() {
		return action.getJDA();
	}
	
	@Override
	public ModalReplyAction setCheck(BooleanSupplier checks) {
		action.setCheck(checks);
		
		return this;
	}
	
	@Override
	public void queue(Consumer<? super Void> success, Consumer<? super Throwable> failure) {
		finish();
		
		action.queue(success, failure);
	}

	@Override
	public Void complete(boolean shouldQueue) throws RateLimitedException {
		finish();
		
		return action.complete(shouldQueue);
	}

	@Override
	public CompletableFuture<Void> submit(boolean shouldQueue) {
		finish();
		
		return action.submit(shouldQueue);
	}

	@Override
	public InteractionCallbackAction<Void> closeResources() {
		return action.closeResources();
	}
	
	private void finish() {
		man.getExecutionData().getManager().addInteractionHandler(modal.getId(), handler);
	}
}
