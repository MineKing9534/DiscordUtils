package de.mineking.discordutils.list;

import org.jetbrains.annotations.NotNull;

public record StringEntry(String entry) implements ListEntry {
	@NotNull
	@Override
	public String build(int index, @NotNull ListContext context) {
		return entry;
	}
}
