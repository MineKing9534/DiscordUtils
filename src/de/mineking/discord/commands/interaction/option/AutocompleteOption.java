package de.mineking.discord.commands.interaction.option;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class AutocompleteOption extends Option {
	public static class AutocompleteData {
		private CommandAutoCompleteInteractionEvent event;
		
		private Member m;
		private Guild g;
		
		private ConcurrentHashMap<String, OptionMapping> args;
		private AutoCompleteQuery current;
		
		public AutocompleteData(CommandAutoCompleteInteractionEvent event, Guild g, Member m, ConcurrentHashMap<String, OptionMapping> args, AutoCompleteQuery current) {
			this.event = event;
			
			this.m = m;
			this.g = g;
			
			this.args = args;
			this.current = current;
		}
		
		public CommandAutoCompleteInteractionEvent getEvent() {
			return event;
		}
		
		public Member getMember() {
			return m;
		}
		
		public Guild getGuild() {
			return g;
		}
		
		public ConcurrentHashMap<String, OptionMapping> getArgs() {
			return args;
		}
		
		public AutoCompleteQuery getCurrent() {
			return current;
		}
	}
	
	private Function<AutocompleteData, List<Choice>> handler;
	
	public AutocompleteOption(OptionType type, String name, String defaultDescription, boolean isRequired, Function<AutocompleteData, List<Choice>> handler) {
		super(type, name, defaultDescription, isRequired);
		
		this.handler = handler;
		setAutoComplete(true);
	}
	
	public AutocompleteOption(OptionType type, String name, String defaultDescription, Function<AutocompleteData, List<Choice>> handler) {
		this(type, name, defaultDescription, false, handler);
	}
	
	public AutocompleteOption(OptionType type, String name, boolean isRequired, Function<AutocompleteData, List<Choice>> handler) {
		this(type, name, " ", isRequired, handler);
	}
	
	public AutocompleteOption(OptionType type, String name, Function<AutocompleteData, List<Choice>> handler) {
		this(type, name, false, handler);
	}
	
	public List<Choice> execute(CommandAutoCompleteInteractionEvent event) {
		if(handler != null) {
			ConcurrentHashMap<String, OptionMapping> args = new ConcurrentHashMap<>();
			
			for(OptionMapping om : event.getOptions()) {
				args.put(om.getName(), om);
			}
			
			return handler.apply(new AutocompleteData(event, event.getGuild(), event.getMember(), args, event.getFocusedOption()));
		}
		
		else {
			return null;
		}
	}
}
