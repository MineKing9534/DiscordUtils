package de.mineking.discord.oauth2.restaction;

import de.mineking.discord.oauth2.OAuth2Manager;
import de.mineking.discord.oauth2.data.OAuth2Tokens;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TokenRetrieveAction extends RestActionImpl<OAuth2Tokens> {
	public enum TokenRetrieveType {
		CODE("authorization_code", "code"),
		REFRESH("refresh_token", "refresh_token");

		private final String type;
		private final String name;

		TokenRetrieveType(String type, String name) {
			this.type = type;
			this.name = name;
		}
	}

	private final OAuth2Manager manager;

	private final String code;
	private final TokenRetrieveType type;

	public TokenRetrieveAction(OAuth2Manager manager, String code, TokenRetrieveType type) {
		super(manager.getManager().getJDA(), OAuth2Routes.GET_TOKENS.compile());

		this.manager = manager;
		this.code = code;
		this.type = type;
	}

	@Override
	protected RequestBody finalizeData() {
		return RequestBody.create(
				"client_id=" + manager.getManager().getJDA().getSelfUser().getId() +
						"&client_secret=" + manager.getConfig().clientSecret +
						"&grant_type=" + type.type +
						"&" + type.name + "=" + code +
						"&redirect_uri=" + manager.getConfig().getRedirectUrl(),
				MediaType.get("application/x-www-form-urlencoded")
		);
	}

	@Override
	protected void handleSuccess(Response response, Request<OAuth2Tokens> request) {
		request.onSuccess(OAuth2Tokens.fromData(manager, response.getObject()));
	}
}
