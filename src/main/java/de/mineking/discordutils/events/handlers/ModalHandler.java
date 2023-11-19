package de.mineking.discordutils.events.handlers;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModalHandler extends FilteredEventHandler<ModalInteractionEvent> {
	public final Consumer<ModalInteractionEvent> handler;

	/**
	 * @param filter  Regex for custom id
	 * @param handler The handler
	 */
	public ModalHandler(@NotNull String filter, @NotNull Consumer<ModalInteractionEvent> handler) {
		super(ModalInteractionEvent.class, event -> filter.isEmpty() || event.getModalId().matches(filter), false);

		Checks.notNull(handler, "handler");

		this.handler = handler;
	}

	@Override
	public void handleEvent(ModalInteractionEvent event) {
		handler.accept(event);
	}
}
