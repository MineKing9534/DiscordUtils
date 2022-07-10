package de.mineking.discord.commands.interaction;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.history.ExecutionData;
import de.mineking.discord.commands.interaction.context.UserContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class UserCommand extends ContextCommand<UserContextInteractionEvent, UserContext> {
	public UserCommand() {
		super(UserContextInteractionEvent.class);
	}
	
	@Override
	protected UserContext buildContext(ExecutionData<UserContextInteractionEvent, UserContext> data) {
		return new UserContext(data);
	}

	@Override
	@Nonnull
	public final CommandData build(Guild g) {
		CommandData data = Commands.user(getName());
		
		if(getFeature().getManager().getLocalizationMapper() != null) {
			data.setNameLocalizations(getFeature().getManager().getLocalizationMapper().apply(getPath()));
		}
		
		return data;
	}
}
