package de.mineking.discord.commands.history;

import javax.annotation.Nonnull;

import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public class RuntimeData extends CommandData {
	public final GenericCommandInteractionEvent event;
	
	public final Member member;
	public final Guild guild;
	
	public RuntimeData(@Nonnull RuntimeData data) {
		super(data);
		
		Checks.nonNull(data, "data");
		
		this.event = data.event;
		
		this.member = data.member;
		this.guild = data.guild;
	}
	
	public RuntimeData(@Nonnull GenericCommandInteractionEvent event) {
		super(event);
		
		this.event = event;
		
		member = event.getMember();
		guild = event.getGuild();
	}
}
