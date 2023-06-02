package de.mineking.discord.linkedroles;

import de.mineking.discord.oauth2.data.OAuth2User;
import de.mineking.discord.oauth2.restaction.OAuth2Action;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserMetaDataUpdateAction extends OAuth2Action<List<UserMetaData>> {
	private final Map<String, Object> data = new HashMap<>();
	private final OAuth2User user;

	public UserMetaDataUpdateAction(OAuth2User user) {
		super(LinkedRolesRoutes.UPDATE_USER_META_DATA.compile(user.getTokens().getManager().getManager().getJDA().getSelfUser().getId()), user.getTokens());
		this.user = user;
	}

	public UserMetaDataUpdateAction put(MetaData meta, Object value) {
		data.put(meta.getKey(), meta.getType().convert(value));
		return this;
	}

	@Override
	protected RequestBody finalizeData() {
		return getRequestBody(
				DataObject.empty()
						.put("platform_name", tokens.getManager().getManager().getJDA().getSelfUser().getName())
						.put("platform_username", user.getGlobalName())
						.put("metadata", data)
		);
	}

	@Override
	protected void handleSuccess(Response response, Request<List<UserMetaData>> request) {
		DataObject metadata = response.getObject().getObject("metadata");

		request.onSuccess(
				metadata.keys().stream()
						.map(tokens.getManager().getManager().getLinkedRolesManager()::findMetaData)
						.filter(Optional::isPresent)
						.map(Optional::get)
						.map(meta ->
								new UserMetaData(
										meta,
										meta.getType().reverse(metadata.get(meta.getKey()))
								)
						)
						.toList()
		);
	}
}
