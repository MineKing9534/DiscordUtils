package de.mineking.discord.commands.interaction.handler;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.List;

public abstract class SelectHandler extends InteractionHandler<SelectMenuInteractionEvent, List<SelectOption>> {
	public SelectHandler(boolean autoRemove) {
		super(autoRemove);
	}

	public SelectHandler() {
		this(false);
	}
	
	@Override
	protected final List<SelectOption> getArguments(SelectMenuInteractionEvent event) {
		return event.getSelectedOptions();
	}
}
