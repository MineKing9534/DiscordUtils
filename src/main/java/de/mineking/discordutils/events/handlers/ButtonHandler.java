package de.mineking.discordutils.events.handlers;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ButtonHandler extends ComponentHandler<ButtonInteractionEvent> {
	/**
	 * @param filter  Regex for custom id
	 * @param handler The handler
	 */
	public ButtonHandler(@NotNull String filter, @NotNull Consumer<ButtonInteractionEvent> handler) {
		super(ButtonInteractionEvent.class, filter, handler);
	}
}
