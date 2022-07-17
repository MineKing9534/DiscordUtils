package de.mineking.discord.commands.interaction.handler;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.Map;
import java.util.stream.Collectors;

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
				.collect(Collectors.toMap(ModalMapping::getId, e -> e));
	}
}
