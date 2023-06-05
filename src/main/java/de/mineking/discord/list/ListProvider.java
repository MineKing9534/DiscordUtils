package de.mineking.discord.list;

import de.mineking.discord.commands.ContextBase;

import java.util.Map;

public interface ListProvider<C extends ContextBase, E extends ListEntry, T extends Listable<E>> {
	T getObject(C context, Map<String, String> params);
}
