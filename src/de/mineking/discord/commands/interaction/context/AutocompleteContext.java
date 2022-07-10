package de.mineking.discord.commands.interaction.context;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.function.Function;
import java.util.function.Supplier;

public class AutocompleteContext {
	public final CommandAutoCompleteInteractionEvent event;
	
	public final Member member;
	public final GuildMessageChannel channel;
	public final Guild guild;
	
	public final AutoCompleteQuery current;
	
	public AutocompleteContext(CommandAutoCompleteInteractionEvent event) {
		this.event = event;
		
		member = event.getMember();
		channel = (GuildMessageChannel)event.getMessageChannel();
		guild = event.getGuild();
		
		current = event.getFocusedOption();
	}
	
	public OptionMapping getOption(String name) {
		return event.getOption(name);
	}

	public <T> T getOption(String name, Function<OptionMapping, T> handler) {
		return event.getOption(name, handler);
	}
	
	public <T> T getOption(String name, T fallback, Function<OptionMapping, T> handler) {
		return event.getOption(name, fallback, handler);
	}
	
	public <T> T getOption(String name, Supplier<T> fallback, Function<OptionMapping, T> handler) {
		return event.getOption(name, fallback, handler);
	}
	
	public boolean hasOption(String name) {
		return getOption(name) != null;
	}
}
