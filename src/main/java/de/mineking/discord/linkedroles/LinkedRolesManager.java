package de.mineking.discord.linkedroles;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import de.mineking.discord.Utils;
import de.mineking.discord.oauth2.OAuth2Scope;
import de.mineking.discord.oauth2.data.OAuth2User;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import okhttp3.RequestBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class LinkedRolesManager extends Module {
	private final List<MetaData> metaData = new ArrayList<>();

	private final BiFunction<OAuth2User, MetaData, Object> handler;
	private final String successUrl;

	public LinkedRolesManager(DiscordUtils manager, String endpoint, String successUrl, BiFunction<OAuth2User, MetaData, Object> handler) {
		super(manager);

		this.successUrl = successUrl;
		this.handler = handler;

		manager.getOAuth2Manager().registerEndpoint(endpoint, new LinkedRolesEndpoint(this));
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public MetaData addMetaData(MetaDataType type, String key, String name, String description) {
		var data = new MetaData(this, type, key, name, null, description, null);
		metaData.add(data);
		return data;
	}

	public MetaData addMetaData(MetaDataType type, String key) {
		var name = manager.getLocalization().getMetaDataName(key);
		var description = manager.getLocalization().getMetaDataDescription(key);

		var nameLocalizations = new LocalizationMap(x -> {
		});
		nameLocalizations.setTranslations(name.values);

		var descriptionLocalizations = new LocalizationMap(x -> {
		});
		descriptionLocalizations.setTranslations(description.values);

		var data = new MetaData(this, type, key, name.defaultValue, nameLocalizations, description.defaultValue, descriptionLocalizations);
		metaData.add(data);
		return data;
	}

	public List<MetaData> getMetaData() {
		return Collections.unmodifiableList(metaData);
	}

	public Optional<MetaData> findMetaData(String key) {
		return metaData.stream()
				.filter(m -> m.getKey().equals(key))
				.findFirst();
	}

	public Optional<UserMetaDataUpdateAction> updateMetaData(String key, OAuth2User user) {
		return findMetaData(key).map(meta -> meta.updateMetaData(user, handler.apply(user, meta)));
	}

	@SuppressWarnings("deprecation")
	public RestAction<List<MetaData>> updateMetaData() {
		return new RestActionImpl<>(manager.getJDA(), Route.Applications.UPDATE_ROLE_CONNECTION_METADATA.compile(manager.getJDA().getSelfUser().getId()),
				RequestBody.create(Requester.MEDIA_TYPE_JSON, DataArray.fromCollection(metaData).toJson()),
				(response, request) -> response.getArray().stream(DataArray::getObject)
						.map(d -> MetaData.fromData(this, d))
						.toList()
		);
	}

	public RestAction<List<MetaData>> retrieveMetaData() {
		return new RestActionImpl<>(manager.getJDA(), Route.Applications.GET_ROLE_CONNECTION_METADATA.compile(manager.getJDA().getSelfUser().getId()),
				(response, request) -> response.getArray().stream(DataArray::getObject)
						.map(d -> MetaData.fromData(this, d))
						.toList()
		);
	}

	public UserMetaDataUpdateAction updateUserMetaData(OAuth2User user) {
		var action = new UserMetaDataUpdateAction(user);

		metaData.forEach(data -> action.put(data, handler.apply(user, data)));

		return action;
	}

	public RestAction<?> updateAllUsers() {
		return manager.getOAuth2Manager().getCredentials().getAll().flatMap(users -> Utils.accumulate(manager.getJDA(), users.stream()
				.filter(user -> user.getTokens().getScopes().contains(OAuth2Scope.RoleConnectionsWrite))
				.map(this::updateUserMetaData)
				.toList()
		));
	}
}
