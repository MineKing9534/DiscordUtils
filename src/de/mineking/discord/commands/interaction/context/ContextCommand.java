package de.mineking.discord.commands.interaction.context;

import javax.annotation.Nullable;

import de.mineking.discord.commands.interaction.Command;

public abstract class ContextCommand extends Command {	
	@Nullable
	public final UserCommand getAsUser() {
		return (this instanceof UserCommand ? (UserCommand)this : null);
	}
	
	@Nullable
	public final MessageCommand getAsMessage() {
		return (this instanceof MessageCommand  ? (MessageCommand)this : null);
	}
}
