package de.mineking.discord.commands.interaction.context;

import de.mineking.discord.commands.history.ExecutionData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public abstract class CommandContext<Event extends GenericCommandInteractionEvent> {
	public final ExecutionData<Event, ? extends CommandContext<Event>> data;
	public final Event event;
	
	public final Member member;
	public final GuildMessageChannel channel;
	public final Guild guild;

	@SuppressWarnings("unchecked")
	public CommandContext(ExecutionData<Event, ? extends CommandContext<Event>> data) {
		this.data = data;
		this.event = (Event)data.event;
		
		member = event.getMember();
		channel = (GuildMessageChannel)event.getMessageChannel();
		guild = event.getGuild();
	}
}
