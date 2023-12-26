package de.mineking.discordutils.restaction;

import de.mineking.discordutils.IOBiFunction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class HttpHost {
	public final CustomRestActionManager manager;

	public final String baseUrl;
	public final CaseInsensitiveMap<String, String> defaultHeaders = new CaseInsensitiveMap<>();

	HttpHost(@NotNull CustomRestActionManager manager, @NotNull String baseUrl) {
		Checks.notNull(manager, "manager");
		Checks.notNull(baseUrl, "baseUrl");

		this.manager = manager;
		this.baseUrl = baseUrl;
	}

	@NotNull
	public <T> CustomRestAction<T> request(@NotNull Route.CompiledRoute route, @NotNull IOBiFunction<CustomRequest<T>, Response, T> handler, @Nullable RequestBody body, @Nullable Headers headers) {
		Checks.notNull(route, "route");
		Checks.notNull(handler, "handler");

		return new CustomRestAction<>(this, route, body, headers == null ? null : headers.headers) {
			@Override
			public T handle(CustomRequest<T> request, Response response) throws IOException {
				return handler.apply(request, response);
			}
		};
	}

	@NotNull
	public <T> CustomRestAction<T> request(@NotNull Route.CompiledRoute route, @NotNull IOBiFunction<CustomRequest<T>, Response, T> handler) {
		return request(route, handler, null, null);
	}

	@NotNull
	public <T> CustomRestAction<T> request(@NotNull Route.CompiledRoute route) {
		return request(route, (request, response) -> null);
	}

	void request(@NotNull CustomRequest<?> request) {
		Checks.notNull(request, "request");

		var builder = new Request.Builder();

		String method = request.route().getMethod().toString();

		var body = request.body() == null && HttpMethod.requiresRequestBody(method) ? Requester.EMPTY_BODY : request.body();

		builder.url(baseUrl + request.route().getCompiledRoute());
		builder.method(method, body);

		defaultHeaders.forEach(builder::header);
		if(request.headers() != null) request.headers().forEach(builder::header);

		var call = manager.client.newCall(builder.build());

		manager.executor.execute(() -> {
			try(var response = call.execute()) {
				if(request.onSuccess() != null) request.handleSuccess(response);
			} catch(Exception e) {
				request.handleError(e);
			}
		});
	}
}
