package de.mineking.exceptions;

public class InvalidNullArgumentException extends RuntimeException {
	public InvalidNullArgumentException(String... fields) {
		super("None of " + String.join(", ", fields) + " may be null");
	}
	
	public InvalidNullArgumentException(String field) {
		super(field + " may not be null");
	}
}
