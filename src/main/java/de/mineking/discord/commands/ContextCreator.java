package de.mineking.discord.commands;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.function.Function;

public class ContextCreator<T extends ContextBase> {
	public final Class<T> type;
	public final Function<GenericInteractionCreateEvent, T> creator;

	public ContextCreator(Class<T> type, Function<GenericInteractionCreateEvent, T> creator) {
		this.type = type;
		this.creator = creator;
	}

	public T createContext(CommandManager<?> manager, GenericInteractionCreateEvent event) {
		var context = creator.apply(event);

		context.manager = manager;

		return context;
	}
}
