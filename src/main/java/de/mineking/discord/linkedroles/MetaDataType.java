package de.mineking.discord.linkedroles;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public enum MetaDataType {
	/**
	 * the metadata value (integer) is less than or equal to the guild's configured value (integer)
	 */
	INTEGER_LESS_THAN_OR_EQUAL(1, Converter.INTEGER),

	/**
	 * the metadata value (integer) is greater than or equal to the guild's configured value (integer)
	 */
	INTEGER_GREATER_THAN_OR_EQUAL(2, Converter.INTEGER),

	/**
	 * the metadata value (integer) is equal to the guild's configured value (integer)
	 */
	INTEGER_EQUAL(3, Converter.INTEGER),

	/**
	 * the metadata value (integer) is not equal to the guild's configured value (integer)
	 */
	INTEGER_NOT_EQUAL(4, Converter.INTEGER),

	/**
	 * the metadata value (ISO8601 string) is less than or equal to the guild's configured value (integer; days before current date)
	 */
	DATETIME_LESS_THAN_OR_EQUAL(5, Converter.DATE),

	/**
	 * the metadata value (ISO8601 string) is greater than or equal to the guild's configured value (integer; days before current date)
	 */
	DATETIME_GREATER_THAN_OR_EQUAL(6, Converter.DATE),

	/**
	 * the metadata value (integer) is equal to the guild's configured value (integer; 1)
	 */
	BOOLEAN_EQUAL(7, Converter.BOOLEAN),

	/**
	 * the metadata value (integer) is not equal to the guild's configured value (integer; 1)
	 */
	BOOLEAN_NOT_EQUAL(8, Converter.BOOLEAN);

	private final int code;
	private final Converter<?> converter;

	MetaDataType(int code, Converter<?> converter) {
		this.code = code;
		this.converter = converter;
	}

	public int getCode() {
		return code;
	}

	public Object convert(Object arg) {
		return converter.handle(arg);
	}

	public Object reverse(Object arg) {
		return converter.reverse(arg);
	}

	public static MetaDataType fromId(int id) {
		for(MetaDataType t : values()) {
			if(t.code == id) {
				return t;
			}
		}

		return null;
	}

	private abstract static class Converter<T> {
		public final static Converter<Integer> INTEGER = new Converter<>(Integer.class) {
			@Override
			protected Object convert(Integer arg) {
				return arg;
			}

			@Override
			protected Integer reverse(Object arg) {
				return Integer.parseInt(arg.toString());
			}
		};

		public final static Converter<Boolean> BOOLEAN = new Converter<>(Boolean.class) {
			@Override
			protected Object convert(Boolean arg) {
				return arg ? 1 : 0;
			}

			@Override
			protected Boolean reverse(Object arg) {
				return Integer.parseInt(arg.toString()) > 0;
			}
		};

		public final static Converter<Instant> DATE = new Converter<>(Instant.class) {
			@Override
			protected Object convert(Instant arg) {
				return arg.truncatedTo(ChronoUnit.SECONDS).toString();
			}

			@Override
			protected Instant reverse(Object arg) {
				return OffsetDateTime.parse(arg.toString()).toInstant();
			}
		};

		protected final Class<T> type;

		public Converter(Class<T> type) {
			this.type = type;
		}

		@SuppressWarnings("unchecked")
		public Object handle(Object arg) {
			if(!type.isAssignableFrom(arg.getClass())) {
				throw new IllegalArgumentException("Provided argument is not compatible");
			}

			return convert((T) arg);
		}

		protected abstract Object convert(T arg);
		protected abstract T reverse(Object arg);
	}
}