package de.mineking.discordutils.list;

public interface ListEntry {
	/**
	 * @param index The index of this entry
	 * @return The string representation of this entry
	 */
	String build(int index);
}
