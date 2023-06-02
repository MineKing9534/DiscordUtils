package de.mineking.discord.oauth2.data;

import de.mineking.discord.oauth2.OAuth2Manager;
import de.mineking.discord.oauth2.OAuth2Scope;
import de.mineking.discord.oauth2.exception.OAuth2Exception;
import de.mineking.discord.oauth2.restaction.OAuth2Action;
import de.mineking.discord.oauth2.restaction.OAuth2Routes;
import de.mineking.discord.oauth2.restaction.TokenRetrieveAction;
import de.mineking.discord.oauth2.restaction.TokenRevokeAction;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.CompletedRestAction;

import java.util.EnumSet;
import java.util.List;

public class OAuth2Tokens {
	private final OAuth2Manager manager;

	private EnumSet<OAuth2Scope> scopes;

	private final String type;
	private String access;
	private String refresh;

	private long expiry;

	public OAuth2Tokens(OAuth2Manager manager, EnumSet<OAuth2Scope> scopes, String type, String access, String refresh, long expiry) {
		this.manager = manager;

		this.scopes = scopes;
		this.type = type;
		this.access = access;
		this.refresh = refresh;
		this.expiry = expiry;
	}

	public static OAuth2Tokens fromData(OAuth2Manager manager, DataObject data) {
		return new OAuth2Tokens(
				manager,
				OAuth2Scope.getScopes(data.getString("scope")),
				data.getString("token_type"),
				data.getString("access_token"),
				data.getString("refresh_token"),
				System.currentTimeMillis() + data.getLong("expires_in") * 1000
		);
	}

	public OAuth2Manager getManager() {
		return manager;
	}

	public long getExpiry() {
		return expiry;
	}

	public EnumSet<OAuth2Scope> getScopes() {
		return scopes;
	}

	public List<String> getScopeList() {
		return scopes.stream().map(s -> s.id).toList();
	}

	public String getType() {
		return type;
	}

	public String getAccessToken() {
		return access;
	}

	public String getRefreshToken() {
		return refresh;
	}

	public String getAuthentication() {
		return type + " " + access;
	}

	public RestAction<String> retrieveAuthentication() {
		return refresh().map(OAuth2Tokens::getAuthentication);
	}

	public RestAction<OAuth2Tokens> refresh() {
		if(System.currentTimeMillis() + 10000 < expiry) {
			return new CompletedRestAction<>(manager.getManager().getJDA(), this);
		}

		if(refresh == null) {
			throw new OAuth2Exception("Cannot refresh token from implicit grant");
		}

		return new TokenRetrieveAction(manager, refresh, TokenRetrieveAction.TokenRetrieveType.REFRESH)
				.onSuccess(token -> {
					scopes = token.scopes;
					expiry = token.expiry;
					access = token.access;
					refresh = token.refresh;
				});
	}

	public RestAction<OAuth2User> retrieveUser() {
		return new OAuth2Action<>(OAuth2Routes.GET_SELF.compile(), this, (response, request) -> OAuth2User.fromData(this, response.getObject()));
	}

	public TokenRevokeAction revoke() {
		return new TokenRevokeAction(this);
	}
}
