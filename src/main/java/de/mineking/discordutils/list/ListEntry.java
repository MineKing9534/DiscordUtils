package de.mineking.discordutils.list;

import org.jetbrains.annotations.NotNull;

public interface ListEntry {
	/**
	 * @param index   The index of this entry
	 * @param context The {@link ListContext}
	 * @return The string representation of this entry
	 */
	@NotNull
	String build(int index, @NotNull ListContext<? extends ListEntry> context);
}
