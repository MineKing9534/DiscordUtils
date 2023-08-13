package de.mineking.discord.commands.annotated.option;

public interface LocalizedEnumOption {
	default String getKey() {
		return toString();
	}
}
