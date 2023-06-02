package de.mineking.discord.ui;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModalFrame extends MenuFrame {
	private final Modal modal;
	private final BiConsumer<Menu, ModalInteractionEvent> handler;
	private CompletableFuture<?> future;

	public ModalFrame(Menu menu, Modal modal, BiConsumer<Menu, ModalInteractionEvent> handler) {
		super(menu);

		this.modal = modal;
		this.handler = handler;
	}

	@Override
	public void show() {
		if(menu.state.modal == null) {
			throw new IllegalStateException();
		}

		menu.state.modal.replyModal(modal).queue();

		future = menu.getEventManager().waitForEvent(ModalInteractionEvent.class, event -> event.getModalId().equals(modal.getId()), Menu.timeout).whenComplete((event, e) -> {
			if(event != null) {
				menu.state.modal = null;
				menu.state.reply = event;

				handler.accept(menu, event);
			}

			else {
				menu.close();
			}
		});
	}

	@Override
	public void cleanup() {
		if(future != null) {
			future.cancel(false);
		}
	}
}
