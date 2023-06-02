package de.mineking.discord.linkedroles;

import java.time.Instant;

public class UserMetaData {
	private final MetaData metaData;
	private final Object value;

	public UserMetaData(MetaData metaData, Object value) {
		this.metaData = metaData;
		this.value = value;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public Object getValue() {
		return value;
	}

	public Integer getValueAsString() {
		return value instanceof Integer i ? i : null;
	}

	public Boolean getValueAsBoolean() {
		return value instanceof Boolean b ? b : null;
	}

	public Instant getValueAsDate() {
		return value instanceof Instant d ? d : null;
	}
}
