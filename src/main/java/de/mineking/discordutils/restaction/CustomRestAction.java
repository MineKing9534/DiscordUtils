package de.mineking.discordutils.restaction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class CustomRestAction<T> implements RestAction<T> {
	private final HttpHost host;
	private final Route.CompiledRoute route;

	private final RequestBody body;
	private final CaseInsensitiveMap<String, String> headers;

	private BooleanSupplier check;

	CustomRestAction(HttpHost host, Route.CompiledRoute route, RequestBody body, CaseInsensitiveMap<String, String> headers) {
		this.host = host;
		this.route = route;

		this.body = body;
		this.headers = headers;
	}

	@NotNull
	public HttpHost getHost() {
		return host;
	}

	@NotNull
	public CustomRestActionManager getManager() {
		return host.manager;
	}

	@NotNull
	@Override
	public JDA getJDA() {
		return host.manager.getManager().jda;
	}

	@NotNull
	@Override
	public CustomRestAction<T> setCheck(@Nullable BooleanSupplier checks) {
		this.check = checks;
		return this;
	}

	@Override
	public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
		if(success == null) success = RestAction.getDefaultSuccess();
		if(failure == null) failure = RestAction.getDefaultFailure();

		host.request(new CustomRequest<>(this, route, success, failure, body, headers, check));
	}

	@Override
	public T complete(boolean shouldQueue) {
		return submit(shouldQueue).join();
	}

	@NotNull
	@Override
	public CompletableFuture<T> submit(boolean shouldQueue) {
		return new CustomRestFuture<>(this, host, route, body, headers, check);
	}

	public abstract T handle(@NotNull CustomRequest<T> request, @NotNull Response response) throws IOException;

	void handleSuccess(@NotNull CustomRequest<T> request, @NotNull Response response) throws IOException {
		if(request.onSuccess() != null) request.onSuccess().accept(handle(request, response));
	}
}
