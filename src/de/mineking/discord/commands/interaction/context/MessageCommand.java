package de.mineking.discord.commands.interaction.context;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class MessageCommand extends ContextCommand {
	@Override
	public void perform() {
		performCommand(getRuntimeData().getMember(), getRuntimeData().getChannel(), ((MessageContextInteractionEvent)getRuntimeData().getEvent()).getTarget());
	}
	
	public void performCommand(Member m, GuildMessageChannel channel, Message target) {}
	
	@Override
	@Nonnull
	public final CommandData build(Guild g) {
		CommandData data = Commands.message(getName());
		
		if(getFeature().getManager().getLocalizationMapper() != null) {
			Map<Locale, String> locales = getFeature().getManager().getLocalizationMapper().apply(getPath());
			
			for(var e : locales.entrySet()) {
				data.setName(e.getValue(), e.getKey());
			}
		}
		
		return data;
	}
}
