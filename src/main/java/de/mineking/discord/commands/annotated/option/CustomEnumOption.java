package de.mineking.discord.commands.annotated.option;

public interface CustomEnumOption {
	default String getName() {
		return toString();
	}
}
