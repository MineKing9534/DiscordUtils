package de.mineking.discord.list;

public interface ListEntry {
	String build(int index, ListContext<?> context);
}
