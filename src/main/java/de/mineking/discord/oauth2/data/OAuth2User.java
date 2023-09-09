package de.mineking.discord.oauth2.data;

import de.mineking.discord.oauth2.restaction.OAuth2Action;
import de.mineking.discord.oauth2.restaction.OAuth2Routes;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class OAuth2User implements UserSnowflake {
	private final OAuth2Tokens tokens;

	private final long id;

	private final String username;
	private final String globalName;

	private final String avatar;
	private final DiscordLocale locale;

	private final int flags;


	public OAuth2User(OAuth2Tokens tokens, long id, String username, String globalName, String avatar, DiscordLocale locale, int flags) {
		this.tokens = tokens;

		this.id = id;
		this.username = username;
		this.globalName = globalName;
		this.avatar = avatar;
		this.locale = locale;
		this.flags = flags;
	}

	public static OAuth2User fromData(OAuth2Tokens tokens, DataObject data) {
		return new OAuth2User(tokens,
				Long.parseLong(data.getString("id")),
				data.getString("username"),
				data.getString("global_name"),
				data.getString("avatar"),
				DiscordLocale.from(data.getString("locale")),
				data.getInt("flags")
		);
	}

	public EnumSet<User.UserFlag> getFlags() {
		return User.UserFlag.getFlags(flags);
	}

	@NotNull
	public OAuth2Tokens getTokens() {
		return tokens;
	}

	@Override
	public long getIdLong() {
		return id;
	}

	@NotNull
	public String getName() {
		return username;
	}

	@NotNull
	public String getGlobalName() {
		return globalName;
	}

	@NotNull
	@Override
	public String getAsMention() {
		return "<@" + getId() + ">";
	}

	@NotNull
	public DiscordLocale getLocale() {
		return locale;
	}

	@Nullable
	public String getAvatarId() {
		return avatar;
	}

	@Nullable
	public String getAvatarUrl() {
		String avatarId = getAvatarId();
		return avatarId == null ? null : String.format(User.AVATAR_URL, getId(), avatarId, avatarId.startsWith("a_") ? "gif" : "png");
	}

	public RestAction<List<UserGuild>> retrieveGuilds() {
		return new OAuth2Action<>(OAuth2Routes.GET_GUILDS.compile(), tokens,
				(response, request) -> response.getArray().stream(DataArray::getObject)
						.map(d -> UserGuild.fromData(this, d))
						.toList()
		);
	}

	@NotNull
	@Override
	public String getDefaultAvatarId() {
		return "";
	}
}
