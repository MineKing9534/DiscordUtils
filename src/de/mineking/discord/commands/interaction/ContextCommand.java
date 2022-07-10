package de.mineking.discord.commands.interaction;

import javax.annotation.Nullable;

import de.mineking.discord.commands.interaction.context.CommandContext;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public abstract class ContextCommand<T extends GenericCommandInteractionEvent, C extends CommandContext<T>> extends Command<T, C> {
	public ContextCommand(Class<T> type) {
		super(type);
	}

	@Nullable
	public final UserCommand getAsUser() {
		return (this instanceof UserCommand ? (UserCommand)this : null);
	}
	
	@Nullable
	public final MessageCommand getAsMessage() {
		return (this instanceof MessageCommand mc ? mc : null);
	}
}
