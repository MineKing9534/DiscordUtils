package de.mineking.discordutils.restaction;

import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class HttpException extends Exception {
	public final Response response;

	public HttpException(@NotNull Response response) throws IOException {
		super("HttpException: [" + response.code() + "] " + response.body().string());
		this.response = response;
	}
}
