package de.mineking.discord.commands.interaction.context;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.history.ExecutionData;

public class UserContext extends CommandContext<UserContextInteractionEvent> {
	public final Member target;
	
	public UserContext(CommandManager cmdMan, ExecutionData<UserContextInteractionEvent, ? extends CommandContext<UserContextInteractionEvent>> data) {
		super(cmdMan, data);
		
		target = event.getTargetMember();
	}
}
