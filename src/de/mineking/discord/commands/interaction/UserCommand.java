package de.mineking.discord.commands.interaction;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import de.mineking.discord.commands.history.ExecutionData;
import de.mineking.discord.commands.interaction.context.UserContext;

public abstract class UserCommand extends ContextCommand<UserContextInteractionEvent, UserContext> {
	public UserCommand() {
		super(UserContextInteractionEvent.class, Command.Type.USER);
	}
	
	@Override
	protected UserContext buildContext(ExecutionData<UserContextInteractionEvent, UserContext> data) {
		return new UserContext(getFeature().getManager(), data);
	}
}
