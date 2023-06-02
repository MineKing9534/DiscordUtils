package de.mineking.discord.events.interaction;

import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SelectionHandler<T, S extends SelectMenu, E extends GenericSelectMenuInteractionEvent<T, S>> extends ComponentHandler<E> {
	public SelectionHandler(Class<E> type, Predicate<E> filter, Consumer<E> handler) {
		super(type, filter, handler);
	}

	public SelectionHandler(Class<E> type, String id, Consumer<E> handler) {
		super(type, id, handler);
	}
}
