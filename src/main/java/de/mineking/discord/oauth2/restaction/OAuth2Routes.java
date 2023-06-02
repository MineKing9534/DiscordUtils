package de.mineking.discord.oauth2.restaction;

import net.dv8tion.jda.api.requests.Route;

public class OAuth2Routes {
	public final static Route GET_SELF = Route.get("users/@me");
	public final static Route GET_GUILDS = Route.get("users/@me/guilds");

	public final static Route GET_TOKENS = Route.post("oauth2/token");
	public final static Route REVOKE_TOKEN = Route.post("oauth2/token/revoke");
}
