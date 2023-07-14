package de.mineking.discord.commands.customrestaction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.function.BiFunction;

public class CustomRestAction<T> extends RestActionImpl<T> {
	private final String host;
	private final Route.CompiledRoute route;

	public CustomRestAction(JDA jda, Route.CompiledRoute route, String host) {
		this(jda, route, host, (RequestBody) null, null);
	}

	public CustomRestAction(JDA jda, Route.CompiledRoute route, String host, DataObject data) {
		this(jda, route, host, data, null);
	}

	public CustomRestAction(JDA jda, Route.CompiledRoute route, String host, RequestBody data) {
		this(jda, route, host, data, null);
	}

	public CustomRestAction(JDA jda, Route.CompiledRoute route, String host, BiFunction<Response, Request<T>, T> handler) {
		this(jda, route, host, (RequestBody) null, handler);
	}

	@SuppressWarnings("deprecation")
	public CustomRestAction(JDA jda, Route.CompiledRoute route, String host, DataObject data, BiFunction<Response, Request<T>, T> handler) {
		this(jda, route, host, data == null ? null : RequestBody.create(Requester.MEDIA_TYPE_JSON, data.toJson()), handler);
		getRequestBody(data);
	}

	public CustomRestAction(JDA jda, Route.CompiledRoute route, String host, RequestBody data, BiFunction<Response, Request<T>, T> handler) {
		super(jda, route, data, handler);
		this.host = host;
		this.route = route;
	}

	@Override
	protected CaseInsensitiveMap<String, String> finalizeHeaders() {
		var headers = new CaseInsensitiveMap<String, String>();

		headers.put("du-host", host);
		headers.put("du-route", route.getCompiledRoute());

		return headers;
	}
}
