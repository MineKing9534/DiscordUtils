package de.mineking.discordutils.localization.text;

import de.mineking.discordutils.ui.Menu;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public interface ITextContainer {
	@NotNull
	String get(@NotNull String name, @NotNull Map<String, Object> args);

	@NotNull
	default String get(@NotNull String name) {
		return get(name, Collections.emptyMap());
	}

	@NotNull
	MessageEmbed buildEmbed(@NotNull String name, @NotNull Map<String, Object> args);

	@NotNull
	default MessageEmbed buildEmbed(@NotNull String name) {
		return buildEmbed(name, Collections.emptyMap());
	}

	@NotNull
	MessageEditData buildMessage(@NotNull String name, @NotNull Map<String, Object> args);

	@NotNull
	default MessageEditData buildMessage(@NotNull String name) {
		return buildMessage(name, Collections.emptyMap());
	}

	@NotNull
	Menu buildMenu(@NotNull String name);
}
