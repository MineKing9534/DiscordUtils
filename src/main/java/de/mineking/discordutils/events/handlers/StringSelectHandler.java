package de.mineking.discordutils.events.handlers;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class StringSelectHandler extends ComponentHandler<StringSelectInteractionEvent> {
	/**
	 * @param filter  Regex for custom id
	 * @param handler The handler
	 */
	public StringSelectHandler(@NotNull String filter, @NotNull Consumer<StringSelectInteractionEvent> handler) {
		super(StringSelectInteractionEvent.class, filter, handler);
	}
}
