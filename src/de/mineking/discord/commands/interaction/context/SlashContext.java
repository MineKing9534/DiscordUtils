package de.mineking.discord.commands.interaction.context;

import java.util.function.Function;
import java.util.function.Supplier;

import de.mineking.discord.commands.history.ExecutionData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SlashContext extends CommandContext<SlashCommandInteractionEvent> {
	public SlashContext(ExecutionData<SlashCommandInteractionEvent, ? extends CommandContext<SlashCommandInteractionEvent>> data) {
		super(data);
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
