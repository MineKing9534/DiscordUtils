package de.mineking.discord.oauth2.endpoint;

import de.mineking.discord.oauth2.OAuth2Manager;
import de.mineking.discord.oauth2.OAuth2Scope;
import de.mineking.discord.oauth2.ResponseType;
import de.mineking.discord.oauth2.data.OAuth2Tokens;
import de.mineking.discord.oauth2.restaction.TokenRetrieveAction;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.HttpStatus;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.internal.requests.CompletedRestAction;

import java.util.EnumSet;

public interface OAuth2Endpoint {
	EnumSet<OAuth2Scope> getScopes();

	void handle(Context context, OAuth2Tokens tokens);

	default ResponseType getOAuth2Type() {
		return ResponseType.IMPLICIT;
	}

	default void handle(OAuth2Manager manager, Context ctx) {
		var action = switch(getOAuth2Type()) {
			case CODE ->
					new TokenRetrieveAction(manager, ctx.queryParam("code"), TokenRetrieveAction.TokenRetrieveType.CODE);
			case IMPLICIT -> new CompletedRestAction<>(manager.getManager().getJDA(), new OAuth2Tokens(manager,
					OAuth2Scope.getScopes(ctx.queryParam("scope")),
					ctx.queryParam("token_type"),
					ctx.queryParam("access_token"),
					null,
					System.currentTimeMillis() + Long.parseLong(ctx.queryParam("expires_in")) * 1000
			));
		};

		action.queue(tokens -> {
			if(!tokens.getScopes().equals(getScopes())) {
				tokens.revoke().queue();
				throw new BadRequestResponse("Invalid scopes");
			}

			this.handle(ctx, tokens);
		}, e -> {
			if(e instanceof ErrorResponseException er) {
				ctx.status(HttpStatus.BAD_REQUEST);
				ctx.result(er.getMeaning());
			}

			else {
				ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		});
	}

	default void handleRegister(String state, Context context) {}

	default void handleError(String state, String error, Context context) throws HttpResponseException {}
}
