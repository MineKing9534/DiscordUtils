package de.mineking.discord.commands.interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.interaction.context.CommandContext;

public abstract class ContextCommand<T extends GenericCommandInteractionEvent, C extends CommandContext<T>> extends Command<T, C> {
	private net.dv8tion.jda.api.interactions.commands.Command.Type type;
	
	public ContextCommand(Class<T> type, net.dv8tion.jda.api.interactions.commands.Command.Type ctype) {
		super(type);
		
		this.type = ctype;
	}

	@Nullable
	public final UserCommand getAsUser() {
		return (this instanceof UserCommand ? (UserCommand)this : null);
	}
	
	@Nullable
	public final MessageCommand getAsMessage() {
		return (this instanceof MessageCommand mc ? mc : null);
	}
	
	@Override
	@Nonnull
	public final CommandData build(Guild g) {
		return new CommandDataImpl(type, getName(), this);
	}
}
