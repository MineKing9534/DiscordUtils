package de.mineking.discordutils.ui;

import de.mineking.discordutils.ui.state.DataState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface MessageRenderer {
	/**
	 * @param function A function to provide a {@link Collection} of {@link MessageEmbed}s to display based on the current {@link DataState}
	 * @return A {@link MessageRenderer} that renders the provided embeds
	 */
	@NotNull
	static MessageRenderer embeds(@NotNull Function<DataState<MessageMenu>, Collection<MessageEmbed>> function) {
		Checks.notNull(function, "function");

		return (state, components) -> new MessageEditBuilder().setEmbeds(function.apply(state)).setComponents(components);
	}

	/**
	 * @param embeds A {@link Collection} of {@link MessageEmbed}s to display
	 * @return A {@link MessageRenderer} that renders the provided embeds
	 */
	@NotNull
	static MessageRenderer embeds(@NotNull Collection<MessageEmbed> embeds) {
		Checks.notNull(embeds, "embeds");

		return (state, components) -> new MessageEditBuilder().setEmbeds(embeds).setComponents(components);
	}

	/**
	 * @param embeds An array of {@link MessageEmbed}s to display
	 * @return A {@link MessageRenderer} that renders the provided embeds
	 */
	@NotNull
	static MessageRenderer embeds(@NotNull MessageEmbed... embeds) {
		Checks.notNull(embeds, "embeds");

		return (state, components) -> new MessageEditBuilder().setEmbeds(embeds).setComponents(components);
	}

	/**
	 * @param function A function to provide a {@link MessageEmbed}s to display based on the current {@link DataState}
	 * @return A {@link MessageRenderer} that renders the provided embeds
	 */
	@NotNull
	static MessageRenderer embed(@NotNull Function<DataState<MessageMenu>, MessageEmbed> function) {
		Checks.notNull(function, "function");

		return (state, components) -> new MessageEditBuilder().setEmbeds(function.apply(state)).setComponents(components);
	}

	/**
	 * @param function A function to provide the content to display based on the current {@link DataState}
	 * @return A {@link MessageRenderer} that renders the provided content
	 */
	@NotNull
	static MessageRenderer content(@NotNull Function<DataState<MessageMenu>, String> function) {
		Checks.notNull(function, "function");

		return (state, components) -> new MessageEditBuilder().setContent(function.apply(state)).setComponents(components);
	}

	/**
	 * @param content The content to display
	 * @return A {@link MessageRenderer} that renders the provided content
	 */
	@NotNull
	static MessageRenderer content(@NotNull String content) {
		Checks.notNull(content, "content");

		return (state, components) -> new MessageEditBuilder().setContent(content).setComponents(components);
	}

	/**
	 * @param function A function to provide a {@link Collection} of {@link FileUpload}s to display based on the current {@link DataState}
	 * @return A {@link MessageRenderer} that renders the provided files
	 */
	@NotNull
	static MessageRenderer files(@NotNull Function<DataState<MessageMenu>, Collection<FileUpload>> function) {
		Checks.notNull(function, "function");

		return (state, components) -> new MessageEditBuilder().setFiles(function.apply(state)).setComponents(components);
	}

	/**
	 * @param files A {@link Collection} of {@link FileUpload}s to display based on the current {@link DataState}
	 * @return A {@link MessageRenderer} that renders the provided files
	 */
	@NotNull
	static MessageRenderer files(@NotNull Collection<FileUpload> files) {
		Checks.notNull(files, "files");

		return (state, components) -> new MessageEditBuilder().setFiles(files).setComponents(components);
	}

	/**
	 * @param files An array of {@link FileUpload}s to display based on the current {@link DataState}
	 * @return A {@link MessageRenderer} that renders the provided files
	 */
	@NotNull
	static MessageRenderer files(@NotNull FileUpload... files) {
		Checks.notNull(files, "files");

		return (state, components) -> new MessageEditBuilder().setFiles(files).setComponents(components);
	}

	/**
	 * @param function A function to provide a {@link FileUpload}s to display based on the current {@link DataState}
	 * @return A {@link MessageRenderer} that renders the provided file
	 */
	@NotNull
	static MessageRenderer file(@NotNull Function<DataState<MessageMenu>, FileUpload> function) {
		Checks.notNull(function, "function");

		return (state, components) -> new MessageEditBuilder().setFiles(function.apply(state)).setComponents(components);
	}

	/**
	 * @param content The content to display
	 * @return A {@link MessageRenderer} with the provided content added
	 */
	@NotNull
	default MessageRenderer withContent(@NotNull String content) {
		Checks.notNull(content, "content");
		return (state, components) -> buildMessage(state, components).setContent(content);
	}

	/**
	 * @param embeds The {@link MessageEmbed}s to display
	 * @return A {@link MessageRenderer} with the provided embeds added
	 */
	@NotNull
	default MessageRenderer withEmbeds(@NotNull MessageEmbed... embeds) {
		Checks.notNull(embeds, "embeds");
		return (state, components) -> buildMessage(state, components).setEmbeds(embeds);
	}

	/**
	 * @param files The {@link FileUpload}s to display
	 * @return A {@link MessageRenderer} with the provided files added
	 */
	@NotNull
	default MessageRenderer withFiles(@NotNull FileUpload... files) {
		Checks.notNull(files, "files");
		return (state, components) -> buildMessage(state, components).setFiles(files);
	}

	/**
	 * @param content A function to provide the content based on the current {@link DataState}
	 * @return A {@link MessageRenderer} with the provided content added
	 */
	@NotNull
	default MessageRenderer withContent(@NotNull Function<DataState<MessageMenu>, String> content) {
		Checks.notNull(content, "content");
		return (state, components) -> buildMessage(state, components).setContent(content.apply(state));
	}

	/**
	 * @param embed A function to provide the {@link MessageEmbed} on the current {@link DataState}
	 * @return A {@link MessageRenderer} with the provided embed added
	 */
	@NotNull
	default MessageRenderer withEmbed(@NotNull Function<DataState<MessageMenu>, MessageEmbed> embed) {
		Checks.notNull(embed, "embed");
		return (state, components) -> buildMessage(state, components).setEmbeds(embed.apply(state));
	}

	/**
	 * @param file A function to provide the {@link FileUpload} based on the current {@link DataState}
	 * @return A {@link MessageRenderer} with the provided file added
	 */
	@NotNull
	default MessageRenderer withFile(@NotNull Function<DataState<MessageMenu>, FileUpload> file) {
		Checks.notNull(file, "file");
		return (state, components) -> buildMessage(state, components).setFiles(file.apply(state));
	}

	/**
	 * @param embeds A function to provide a {@link Collection} of {@link MessageEmbed}s based on the current {@link DataState}
	 * @return A {@link MessageRenderer} with the provided embeds added
	 */
	@NotNull
	default MessageRenderer withEmbeds(@NotNull Function<DataState<MessageMenu>, Collection<MessageEmbed>> embeds) {
		Checks.notNull(embeds, "embeds");
		return (state, components) -> buildMessage(state, components).setEmbeds(embeds.apply(state));
	}

	/**
	 * @param files A function to provide a {@link Collection} of {@link FileUpload}s based on the current {@link DataState}
	 * @return A {@link MessageRenderer} with the provided files added
	 */
	@NotNull
	default MessageRenderer withFiles(@NotNull Function<DataState<MessageMenu>, Collection<FileUpload>> files) {
		Checks.notNull(files, "files");
		return (state, components) -> buildMessage(state, components).setFiles(files.apply(state));
	}

	@NotNull
	MessageEditBuilder buildMessage(@NotNull DataState<MessageMenu> state, @NotNull List<ActionRow> components);
}
