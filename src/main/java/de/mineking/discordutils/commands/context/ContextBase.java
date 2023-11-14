package de.mineking.discordutils.commands.context;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

public abstract class ContextBase<E extends GenericInteractionCreateEvent> {
	@NotNull
	public final E event;

	public ContextBase(@NotNull E event) {
		Checks.notNull(event, "event");

		this.event = event;
	}
}
