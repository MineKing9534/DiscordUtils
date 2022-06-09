package de.mineking.discord.commands.interaction.context;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class UserCommand extends ContextCommand {
	@Override
	public void perform() {
		performCommand(getRuntimeData().getMember(), getRuntimeData().getChannel(), ((UserContextInteractionEvent)getRuntimeData().getEvent()).getTargetMember());
	}
	
	public void performCommand(Member m, GuildMessageChannel channel, Member target) {}
	
	@Override
	@Nonnull
	public final CommandData build(Guild g) {
		CommandData data = Commands.user(getName());
		
		if(getFeature().getManager().getLocalizationMapper() != null) {
			Map<Locale, String> locales = getFeature().getManager().getLocalizationMapper().apply(getPath());
			
			for(var e : locales.entrySet()) {
				data.setName(e.getValue(), e.getKey());
			}
		}
		
		return data;
	}
}
