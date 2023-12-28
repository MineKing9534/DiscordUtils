package de.mineking.discordutils.localization.text;

import de.mineking.discordutils.ui.Menu;
import groovy.util.ResourceException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.simpleyaml.configuration.implementation.snakeyaml.SnakeYamlImplementation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class TextFile implements ITextContainer {
	private final TextManager manager;
	private final String path;
	private final YamlConfiguration yaml;

	private final DiscordLocale locale;

	private TextFile(TextManager manager, String path, DiscordLocale locale) throws IOException, URISyntaxException {
		this.manager = manager;
		this.path = path;

		this.yaml = new YamlConfiguration(new SnakeYamlImplementation());
		this.yaml.load(() -> getClass().getResourceAsStream("/" + path));

		this.locale = locale;
	}

	public static TextFile read(TextManager manager, String path, DiscordLocale locale) throws ResourceException {
		try {
			if(TextFile.class.getResource("/" + path) == null) return null;

			return new TextFile(manager, path, locale);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}


	@NotNull
	@Override
	public String get(@NotNull String name, @NotNull Map<String, Object> args) {
		return null;
	}

	@NotNull
	@Override
	public MessageEmbed buildEmbed(@NotNull String name, @NotNull Map<String, Object> args) {
		return null;
	}

	@NotNull
	@Override
	public MessageEditData buildMessage(@NotNull String name, @NotNull Map<String, Object> args) {
		return null;
	}

	@NotNull
	@Override
	public Menu buildMenu(@NotNull String name) {
		return null;
	}
}
