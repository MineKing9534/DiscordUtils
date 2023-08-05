package de.mineking.discord.commands.annotated;

public interface OptionEnum {
	default String getName() {
		return toString();
	}
}
