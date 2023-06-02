package de.mineking.discord.linkedroles;

import de.mineking.discord.oauth2.data.OAuth2Tokens;
import de.mineking.discord.oauth2.data.OAuth2User;
import de.mineking.discord.oauth2.restaction.OAuth2Action;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.localization.LocalizationUtils;
import org.jetbrains.annotations.NotNull;

public class MetaData implements SerializableData {
	private final LinkedRolesManager manager;

	private final MetaDataType type;
	private final String key;

	private final String name;
	private final String description;

	private final LocalizationMap nameLocalizations;
	private final LocalizationMap descriptionLocalizations;

	public MetaData(LinkedRolesManager manager, MetaDataType type, String key, String name, LocalizationMap nameLocalizations, String description, LocalizationMap descriptionLocalizations) {
		this.manager = manager;

		this.type = type;
		this.key = key;

		this.name = name;
		this.nameLocalizations = nameLocalizations;

		this.description = description;
		this.descriptionLocalizations = descriptionLocalizations;
	}

	public static MetaData fromData(LinkedRolesManager manager, DataObject data) {
		return new MetaData(
				manager,
				MetaDataType.fromId(data.getInt("type")),
				data.getString("key"),
				data.getString("name"),
				LocalizationUtils.unmodifiableFromProperty(data, "name_localizations"),
				data.getString("description"),
				LocalizationUtils.unmodifiableFromProperty(data, "description_localizations")
		);
	}

	public MetaDataType getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public UserMetaDataUpdateAction updateMetaData(OAuth2User user, Object value) {
		return new UserMetaDataUpdateAction(user).put(this, value);
	}

	@NotNull
	@Override
	public DataObject toData() {
		return DataObject.empty()
				.put("type", type.getCode())
				.put("key", key)
				.put("name", name)
				.put("name_localizations", nameLocalizations)
				.put("description", description)
				.put("description_localizations", descriptionLocalizations);
	}
}
