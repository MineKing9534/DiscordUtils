package de.mineking.discord.ui;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModalFrame extends ModalFrameBase {
	private final Function<String, Modal> modal;
	private final BiConsumer<MenuBase, ModalInteractionEvent> handler;

	public ModalFrame(Menu menu, Function<String, Modal> modal, BiConsumer<MenuBase, ModalInteractionEvent> handler) {
		super(menu);
		this.modal = modal;
		this.handler = handler;
	}

	@Override
	public Modal getModal(String id) {
		return modal.apply(id);
	}

	@Override
	public void handle(MenuBase menu, ModalInteractionEvent event) {
		handler.accept(menu, event);
	}
}
