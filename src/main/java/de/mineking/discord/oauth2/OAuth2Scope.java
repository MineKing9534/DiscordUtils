package de.mineking.discord.oauth2;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OAuth2Scope {
	/**
	 * allows your app to fetch data from a user's "Now Playing/Recently Played" list - requires Discord approval
	 */
	ActivitiesRead("activities.read"),

	/**
	 * allows your app to update a user's activity - requires Discord approval (NOT REQUIRED FOR <a href="https://discord.com/developers/docs/game-sdk/activities">GAMESDK ACTIVITY MANAGER</a>)
	 */
	ActivitiesWrite("activities.write"),

	/**
	 * allows your app to read build data for a user's applications
	 */
	ApplicationsBuildsRead("applications.builds.read"),

	/**
	 * allows your app to upload/update builds for a user's applications - requires Discord approval
	 */
	ApplicationsBuildsUpload("applications.builds.upload"),

	/**
	 * allows your app to use <a href="https://discord.com/developers/docs/interactions/application-commands">commands</a> in a guild
	 */
	ApplicationsCommands("applications.commands"),

	/**
	 * allows your app to update its <a href="https://discord.com/developers/docs/interactions/application-commands">commands</a> using a Bearer token - <a href="https://discord.com/developers/docs/topics/oauth2#client-credentials-grant">client credentials grant</a> only
	 */
	ApplicationsCommandsUpdate("applications.commands.update"),

	/**
	 * allows your app to update <a href="https://discord.com/developers/docs/interactions/application-commands#permissions">permissions for its commands</a> in a guild a user has permissions to
	 */
	ApplicationsCommandsPermissionsUpdate("applications.commands.permissions.update"),

	/**
	 * allows your app to read entitlements for a user's applications
	 */
	ApplicationsEntitlements("applications.entitlements"),

	/**
	 * allows your app to read and update store data (SKUs, store listings, achievements, etc.) for a user's applications
	 */
	ApplicationsStoreUpdate("applications.store.update"),

	/**
	 * for oauth2 bots, this puts the bot in the user's selected guild by default
	 */
	Bot("bot"),

	/**
	 * allows <a href="https://discord.com/developers/docs/resources/user#get-user-connections">/users/@me/connections</a> to return linked third-party accounts
	 */
	Connections("connections"),

	/**
	 * allows your app to see information about the user's DMs and group DMs - requires Discord approval
	 */
	DmChannelsRead("dm_channels.read"),

	/**
	 * enables <a href="https://discord.com/developers/docs/resources/user#get-current-user">/users/@me</a> to return an email
	 */
	EMail("email"),

	/**
	 * allows your app to <a href="https://discord.com/developers/docs/resources/channel#group-dm-add-recipient">join users to a group dm</a>
	 */
	GdmJoin("gdm.join"),

	/**
	 * allows <a href="https://discord.com/developers/docs/resources/user#get-current-user-guilds">/users/@me/guilds</a> to return basic information about all of a user's guilds
	 */
	Guilds("guilds"),

	/**
	 * allows <a href="https://discord.com/developers/docs/resources/guild#add-guild-member">/guilds/{guild.id}/members/{user.id}</a> to be used for joining users to a guild
	 */
	GuildsJoin("guilds.join"),

	/**
	 * allows <a href="https://discord.com/developers/docs/resources/user#get-current-user-guild-member">/users/@me/guilds/{guild.id}/member</a> to return a user's member information in a guild
	 */
	GuildsMembersRead("guilds.members.read"),

	/**
	 * allows <a href="https://discord.com/developers/docs/resources/user#get-current-user">/users/@me </a> without email
	 */
	Identify("identify"),

	/**
	 * for local rpc server api access, this allows you to read messages from all client channels (otherwise restricted to channels/guilds your app creates)
	 */
	MessagesRead("messages.read"),

	/**
	 * allows your app to know a user's friends and implicit relationships - requires Discord approval
	 */
	RelationshipsRead("relationships.read"),

	/**
	 * allows your app to update a user's connection and metadata for the app
	 */
	RoleConnectionsWrite("role_connections.write"),

	/**
	 * for local rpc server access, this allows you to control a user's local Discord client - requires Discord approval
	 */
	RPC("rpc"),

	/**
	 * for local rpc server access, this allows you to update a user's activity - requires Discord approval
	 */
	RPCActivitiesWrite("rpc.activities.write"),

	/**
	 * for local rpc server access, this allows you to receive notifications pushed out to the user - requires Discord approval
	 */
	RPCNotificationsRead("rpc.notifications.read"),

	/**
	 * for local rpc server access, this allows you to read a user's voice settings and listen for voice events - requires Discord approval
	 */
	RPCVoiceRead("rpc.voice.read"),

	/**
	 * for local rpc server access, this allows you to update a user's voice settings - requires Discord approval
	 */
	RPCVoiceWrite("rpc.voice.write"),

	/**
	 * allows your app to connect to voice on user's behalf and see all the voice members - requires Discord approval
	 */
	Voice("voice"),

	/**
	 * this generates a webhook that is returned in the oauth token response for authorization code grants
	 */
	WebhookIncoming("webhook.incoming");

	public final String id;

	OAuth2Scope(String id) {
		this.id = id;
	}

	public static EnumSet<OAuth2Scope> getScopes(String scope) {
		return getScopes(scope.split(" "));
	}

	public static EnumSet<OAuth2Scope> getScopes(String[] scopes) {
		return Stream.of(scopes).map(OAuth2Scope::get).collect(Collectors.toCollection(() -> EnumSet.noneOf(OAuth2Scope.class)));
	}

	public static OAuth2Scope get(String id) {
		return EnumSet.allOf(OAuth2Scope.class).stream()
				.filter(s -> s.id.equals(id))
				.findAny().orElse(null);
	}

	public static String build(OAuth2Scope... scopes) {
		return build(Arrays.asList(scopes));
	}

	public static String build(Collection<OAuth2Scope> scopes) {
		return build(EnumSet.copyOf(scopes));
	}

	public static String build(EnumSet<OAuth2Scope> scopes) {
		return String.join(" ", scopes.stream().map(s -> s.id).toList());
	}
}