package de.mineking.discord.ui;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public abstract class ModalFrameBase extends MenuFrame {
	private CompletableFuture<ModalInteractionEvent> future;

	public ModalFrameBase(Menu menu) {
		super(menu);
	}

	public abstract Modal getModal(String id);
	public abstract void handle(MenuBase menu, ModalInteractionEvent event);

	@Override
	public void render() {
		if(menu.state.modal == null) throw new IllegalStateException();

		var modal = getModal(menu.getId() + ":" + getName());
		menu.state.modal.replyModal(modal).queue();

		future = menu.getEventManager().waitForEvent(ModalInteractionEvent.class, event -> event.getModalId().equals(modal.getId()), Menu.timeout);

		future.whenComplete((event, e) -> {
			if(event != null) {
				menu.handle(event);

				try {
					handle(menu, event);
				} catch(Exception ex) {
					Menu.logger.error("Failed to handle ModalFrame '" + name + "'", e);
				}
			} else if(e instanceof TimeoutException) menu.close();
		});
	}

	@Override
	public void cleanup() {
		if(future != null) {
			future.cancel(true);
		}
	}
}
