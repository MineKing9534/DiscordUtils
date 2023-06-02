package de.mineking.discord.oauth2.data;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.List;

public class UserGuild implements ISnowflake {
	private final OAuth2User user;

	private final boolean owner;
	private final long id;
	private final String name;
	private final String icon;
	private final List<Permission> permissions;

	private UserGuild(OAuth2User user, boolean owner, long id, String name, String icon, List<Permission> permissions) {
		this.user = user;
		this.owner = owner;
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.permissions = permissions;
	}

	public static UserGuild fromData(OAuth2User user, DataObject data) {
		return new UserGuild(user,
				data.getBoolean("owner"),
				Long.parseLong(data.getString("id")),
				data.getString("name"),
				data.getString("icon", null),
				Permission.getPermissions(Long.parseLong(data.getString("permissions"))).stream().toList()
		);
	}

	public OAuth2User getUser() {
		return user;
	}

	public boolean isOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public String getIconId() {
		return icon;
	}

	public String getIconUrl() {
		String iconId = this.getIconId();
		return iconId == null ? null : String.format("https://cdn.discordapp.com/icons/%s/%s.%s", this.getId(), iconId, iconId.startsWith("a_") ? "gif" : "png");
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	@Override
	public long getIdLong() {
		return id;
	}
}