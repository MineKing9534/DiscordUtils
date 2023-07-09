package de.mineking.discord.events.interaction;

import de.mineking.discord.events.EventHandlerBase;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModalHandler extends EventHandlerBase<ModalInteractionEvent> {
	private final Predicate<ModalInteractionEvent> filter;
	private final Consumer<ModalInteractionEvent> handler;

	public ModalHandler(Predicate<ModalInteractionEvent> filter, Consumer<ModalInteractionEvent> handler) {
		super(ModalInteractionEvent.class);

		this.filter = filter;
		this.handler = handler;
	}

	public ModalHandler(String id, Consumer<ModalInteractionEvent> handler) {
		this(event -> event.getModalId().matches(id), handler);
	}

	@Override
	public void handleEvent(ModalInteractionEvent event) {
		if(filter.test(event)) {
			handler.accept(event);
		}
	}
}
