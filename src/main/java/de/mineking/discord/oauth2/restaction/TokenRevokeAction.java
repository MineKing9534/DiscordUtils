package de.mineking.discord.oauth2.restaction;

import de.mineking.discord.oauth2.data.OAuth2Tokens;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TokenRevokeAction extends OAuth2Action<Void> {
	public TokenRevokeAction(OAuth2Tokens tokens) {
		super(OAuth2Routes.REVOKE_TOKEN.compile(), tokens);
	}

	@Override
	protected RequestBody finalizeData() {
		return RequestBody.create(
				"client_id=" + tokens.getManager().getManager().getJDA().getSelfUser().getId() +
						"&client_secret=" + tokens.getManager().getConfig().clientSecret +
						"&token=" + tokens.getAccessToken(),
				MediaType.get("application/x-www-form-urlencoded")
		);
	}

	@Override
	protected void handleSuccess(Response response, Request<Void> request) {
		tokens.getManager().getCredentials().removeUser(tokens.getAccessToken());
	}
}
