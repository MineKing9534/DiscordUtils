package de.mineking.discord.oauth2;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class OAuth2Config {
	public final Javalin server;

	public final String clientSecret;

	public final String host;
	public final String redirectPath;

	public OAuth2Config(Javalin server, String clientSecret, String host, String redirectPath) {
		this.server = server;
		this.clientSecret = clientSecret;

		this.host = host;
		this.redirectPath = redirectPath;
	}

	public OAuth2Config(String clientSecret, String host, String redirectPath) {
		this(Javalin.create(cfg -> cfg.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost))), clientSecret, host, redirectPath);
	}

	public String getRedirectUrl() {
		return host + "/" + redirectPath;
	}
}
