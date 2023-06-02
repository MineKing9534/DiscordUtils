package de.mineking.discord.oauth2;

import de.mineking.discord.oauth2.data.OAuth2User;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.List;

public interface CredentialsManager {
	void putUser(OAuth2User user);

	void removeUser(String accessToken);

	RestAction<List<OAuth2User>> getAll();
}
