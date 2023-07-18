package de.mineking.discord.customrestaction;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Requester;
import okhttp3.RequestBody;

import java.util.function.BiFunction;

public class CustomRestActionManager extends Module {
	public CustomRestActionManager(DiscordUtils manager) {
		super(manager);
	}

	public CustomRestAction<Void> request(String host, Route.CompiledRoute route) {
		return new CustomRestAction<>(manager.getJDA(), route, host);
	}

	public CustomRestAction<Void> request(String host, Route.CompiledRoute route, DataObject data) {
		return new CustomRestAction<>(manager.getJDA(), route, host, data);
	}

	public CustomRestAction<Void> request(String host, Route.CompiledRoute route, RequestBody data) {
		return new CustomRestAction<>(manager.getJDA(), route, host, data, null);
	}

	public <T> CustomRestAction<T> request(String host, Route.CompiledRoute route, Class<T> type, BiFunction<Response, Request<T>, T> handler) {
		return new CustomRestAction<>(manager.getJDA(), route, host, (RequestBody) null, handler);
	}

	@SuppressWarnings("deprecation")
	public <T> CustomRestAction<T> request(String host, Route.CompiledRoute route, DataObject data, Class<T> type, BiFunction<Response, Request<T>, T> handler) {
		return new CustomRestAction<>(manager.getJDA(), route, host, data == null ? null : RequestBody.create(Requester.MEDIA_TYPE_JSON, data.toJson()), handler);
	}

	public <T> CustomRestAction<T> request(String host, Route.CompiledRoute route, RequestBody data, Class<T> type, BiFunction<Response, Request<T>, T> handler) {
		return new CustomRestAction<>(manager.getJDA(), route, host, data, handler);
	}
}
