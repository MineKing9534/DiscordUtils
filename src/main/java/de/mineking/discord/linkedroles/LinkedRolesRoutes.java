package de.mineking.discord.linkedroles;

import net.dv8tion.jda.api.requests.Route;

public class LinkedRolesRoutes {
	public final static Route UPDATE_USER_META_DATA = Route.put("users/@me/applications/{application_id}/role-connection");
	public final static Route GET_USER_META_DATA = Route.get("/users/@me/applications/{application_id}/role-connection");
}
