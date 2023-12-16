package de.mineking.discordutils.restaction;

import net.dv8tion.jda.api.requests.Route;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CustomRestFuture<T> extends CompletableFuture<T> {
	private final CustomRequest<T> request;
	private final HttpHost host;

	CustomRestFuture(CustomRestAction<T> action, HttpHost host, Route.CompiledRoute route, RequestBody body, CaseInsensitiveMap<String, String> headers) {
		this.host = host;

		host.request(request = new CustomRequest<>(action, route, this::complete, this::completeExceptionally, body, headers));
	}

	@NotNull
	public CustomRequest<T> getAction() {
		return request;
	}

	@NotNull
	public HttpHost getHost() {
		return host;
	}

	@NotNull
	public CustomRestActionManager getManager() {
		return host.manager;
	}
}
