package de.mineking.discordutils.list;

import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.state.DataState;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Listable<T extends ListEntry> {
	/**
	 * @param state   The {@link DataState} holding information about the current state of the list ui
	 * @param context The {@link ListContext}
	 * @return All entries
	 */
	@NotNull
	List<T> getEntries(@NotNull DataState<MessageMenu> state, @NotNull ListContext<T> context);

	/**
	 * @return The number of entries to display on a single page
	 */
	default int entriesPerPage() {
		return 20;
	}

	/**
	 * @param state   The {@link DataState} holding information about the current state of the list ui
	 * @param context The {@link ListContext}
	 * @return An {@link EmbedBuilder}
	 */
	@NotNull
	default EmbedBuilder createEmbed(@NotNull DataState<MessageMenu> state, @NotNull ListContext<T> context) {
		return new EmbedBuilder();
	}

	/**
	 * @param state   The {@link DataState} holding information about the current state of the list ui
	 * @param context The {@link ListContext}
	 * @return The {@link MessageEmbed} to display
	 */
	@NotNull
	default MessageEmbed buildEmbed(@NotNull DataState<MessageMenu> state, @NotNull ListContext<T> context) {
		var embed = createEmbed(state, context);

		int page = state.getState("page");

		System.out.println("Render: " + state);

		if(!context.entries().isEmpty()) {
			int i = ((page - 1) * entriesPerPage());
			for(var e : context.entries()) embed.appendDescription(e.build(i++, context) + "\n");
		}

		return embed.build();
	}
}
