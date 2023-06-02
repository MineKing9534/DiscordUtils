package de.mineking.discord.oauth2;

public enum ResponseType {
	IMPLICIT("token"),
	CODE("code");

	public final String name;

	ResponseType(String name) {
		this.name = name;
	}
}
