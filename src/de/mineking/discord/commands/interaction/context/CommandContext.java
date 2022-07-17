package de.mineking.discord.commands.interaction.context;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.history.ExecutionData;

public abstract class CommandContext<Event extends GenericCommandInteractionEvent> {
	public final CommandManager cmdMan;
	
	public final ExecutionData<Event, ? extends CommandContext<Event>> data;
	public final Event event;
	
	public final Member member;
	public final GuildMessageChannel channel;
	public final Guild guild;

	@SuppressWarnings("unchecked")
	public CommandContext(CommandManager cmdMan, ExecutionData<Event, ? extends CommandContext<Event>> data) {
		this.cmdMan = cmdMan;
		
		this.data = data;
		this.event = (Event)data.event;
		
		member = event.getMember();
		channel = (GuildMessageChannel)event.getMessageChannel();
		guild = event.getGuild();
	}
}
