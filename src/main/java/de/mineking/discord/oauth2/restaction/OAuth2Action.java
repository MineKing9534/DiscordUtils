package de.mineking.discord.oauth2.restaction;

import de.mineking.discord.oauth2.data.OAuth2Tokens;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.function.BiFunction;

public class OAuth2Action<T> extends RestActionImpl<T> {
	protected final OAuth2Tokens tokens;

	public OAuth2Action(Route.CompiledRoute route, OAuth2Tokens tokens) {
		this(route, tokens, (RequestBody) null, null);
	}

	public OAuth2Action(Route.CompiledRoute route, OAuth2Tokens tokens, DataObject data) {
		this(route, tokens, data, null);
	}

	public OAuth2Action(Route.CompiledRoute route, OAuth2Tokens tokens, RequestBody data) {
		this(route, tokens, data, null);
	}

	public OAuth2Action(Route.CompiledRoute route, OAuth2Tokens tokens, BiFunction<Response, Request<T>, T> handler) {
		this(route, tokens, (RequestBody) null, handler);
	}

	@SuppressWarnings("deprecation")
	public OAuth2Action(Route.CompiledRoute route, OAuth2Tokens tokens, DataObject data, BiFunction<Response, Request<T>, T> handler) {
		this(route, tokens, data == null ? null : RequestBody.create(Requester.MEDIA_TYPE_JSON, data.toJson()), handler);
		getRequestBody(data);
	}

	public OAuth2Action(Route.CompiledRoute route, OAuth2Tokens tokens, RequestBody data, BiFunction<Response, Request<T>, T> handler) {
		super(tokens.getManager().getManager().getJDA(), route, data, handler);

		this.tokens = tokens;
	}

	@Override
	protected CaseInsensitiveMap<String, String> finalizeHeaders() {
		CaseInsensitiveMap<String, String> headers = new CaseInsensitiveMap<>();

		headers.put("Authorization", tokens.getAuthentication());

		return headers;
	}
}
