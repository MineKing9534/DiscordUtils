package de.mineking.discord.commands.interaction.context;

import de.mineking.discord.commands.history.ExecutionData;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public class UserContext extends Context<UserContextInteractionEvent> {
	public final Member target;
	
	public UserContext(ExecutionData<UserContextInteractionEvent, ? extends Context<UserContextInteractionEvent>> data) {
		super(data);
		
		target = event.getTargetMember();
	}
}
