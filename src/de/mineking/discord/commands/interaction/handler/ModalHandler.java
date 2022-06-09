package de.mineking.discord.commands.interaction.handler;

import java.util.Map;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public abstract class ModalHandler extends InteractionHandler<ModalInteractionEvent, Map<String, ModalMapping>> {
	public ModalHandler(boolean autoRemove) {
		super(autoRemove);
	}
	
	public ModalHandler() {
		this(false);
	}

	@Override
	protected Map<String, ModalMapping> getArguments(ModalInteractionEvent event) {
		return event.getValues().stream()
				.collect(Collectors.toMap((e) -> e.getId(), (e) -> e));
	}
}
