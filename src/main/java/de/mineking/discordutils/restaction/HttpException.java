package de.mineking.discordutils.restaction;

public class HttpException extends Exception {
	public final int code;
	public final String message;

	public HttpException(int code, String message) {
		super("HttpException: [" + code + "] " + message);
		this.code = code;
		this.message = message;
	}
}
