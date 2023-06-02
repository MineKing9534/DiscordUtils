package de.mineking.discord.oauth2;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import de.mineking.discord.oauth2.data.OAuth2Tokens;
import de.mineking.discord.oauth2.endpoint.OAuth2Endpoint;
import de.mineking.discord.oauth2.restaction.TokenRetrieveAction;
import io.javalin.http.BadRequestResponse;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Random;

public class OAuth2Manager extends Module {
	public final static Logger logger = LoggerFactory.getLogger(OAuth2Manager.class);

	private final static Random random = new Random();

	private final OAuth2Config config;
	private final CredentialsManager credentials;

	private final Cache<String, OAuth2Endpoint> states = Caffeine.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(10))
			.build();

	public OAuth2Manager(DiscordUtils manager, OAuth2Config config, CredentialsManager credentials) {
		super(manager);

		this.config = config;
		this.credentials = credentials;

		config.server.get(config.redirectPath, ctx -> {
			var state = ctx.queryParam("state");

			if(state == null || state.isEmpty()) {
				throw new BadRequestResponse();
			}

			state = URLDecoder.decode(state, StandardCharsets.UTF_8);

			var endpoint = states.getIfPresent(state);

			if(endpoint == null) {
				throw new BadRequestResponse();
			}

			var error = ctx.queryParam("error");

			if(error != null) {
				endpoint.handleError(state, error, ctx);
			}

			else {
				endpoint.handle(this, ctx);
			}
		});
	}

	public OAuth2Config getConfig() {
		return config;
	}

	public CredentialsManager getCredentials() {
		return credentials;
	}

	private String generateState() {
		var temp = new byte[50];
		String state;

		do {
			random.nextBytes(temp);
			state = Base64.getUrlEncoder().encodeToString(temp);
		} while(states.asMap().containsKey(state));

		return state;
	}

	public OAuth2Manager registerEndpoint(String endpoint, OAuth2Endpoint handler) {
		config.server.get(endpoint, ctx -> {
			var state = generateState();

			states.put(state, handler);
			handler.handleRegister(state, ctx);

			ctx.redirect(getAuthorizationUrl(state, handler.getScopes()));
		});

		return this;
	}

	public String getAuthorizationUrl(String state, EnumSet<OAuth2Scope> scopes) {
		return "https://discord.com/api/oauth2/authorize?" +
				"client_id=" + manager.getJDA().getSelfUser().getId() +
				"&redirect_uri=" + config.getRedirectUrl() +
				"&response_type=code" +
				"&state=" + state +
				"&prompt=consent" +
				"&scope=" + URLEncoder.encode(OAuth2Scope.build(scopes), StandardCharsets.UTF_8);
	}

	public RestAction<OAuth2Tokens> retrieveTokens(String code) {
		return new TokenRetrieveAction(this, code, TokenRetrieveAction.TokenRetrieveType.CODE);
	}
}
