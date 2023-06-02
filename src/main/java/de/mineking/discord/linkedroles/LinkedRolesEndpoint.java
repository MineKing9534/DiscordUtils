package de.mineking.discord.linkedroles;

import de.mineking.discord.oauth2.OAuth2Manager;
import de.mineking.discord.oauth2.OAuth2Scope;
import de.mineking.discord.oauth2.ResponseType;
import de.mineking.discord.oauth2.data.OAuth2Tokens;
import de.mineking.discord.oauth2.endpoint.OAuth2Endpoint;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.EnumSet;

public class LinkedRolesEndpoint implements OAuth2Endpoint {
	private final LinkedRolesManager manager;

	public LinkedRolesEndpoint(LinkedRolesManager manager) {
		this.manager = manager;
	}

	@Override
	public EnumSet<OAuth2Scope> getScopes() {
		return EnumSet.of(OAuth2Scope.Identify, OAuth2Scope.RoleConnectionsWrite);
	}

	@Override
	public ResponseType getOAuth2Type() {
		return ResponseType.CODE;
	}

	@Override
	public void handle(Context context, OAuth2Tokens tokens) {
		tokens.retrieveUser().queue(user -> {
			try {
				manager.getManager().getOAuth2Manager().getCredentials().putUser(user);
				manager.updateUserMetaData(user).queue();

				context.redirect(manager.getSuccessUrl());
			} catch(Exception e) {
				OAuth2Manager.logger.error("Handling linked roles failed", e);
				tokens.revoke().queue();
				context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		});
	}
}
