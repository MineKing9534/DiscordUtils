package de.mineking.discordutils.list;

import de.mineking.discordutils.ui.state.DataState;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Listable<T extends ListEntry> {
	/**
	 * @param state The {@link DataState} holding information about the current state of the list ui
	 * @return All entries
	 */
	@NotNull
	List<T> getEntries(@NotNull DataState state);

	/**
	 * @return The number of entries to display on a single page
	 */
	default int entriesPerPage() {
		return 20;
	}

	/**
	 * @param state The {@link DataState} holding information about the current state of the list ui
	 * @return An {@link EmbedBuilder}
	 */
	@NotNull
	default EmbedBuilder createEmbed(@NotNull DataState state) {
		return new EmbedBuilder();
	}

	/**
	 * @param state The {@link DataState} holding information about the current state of the list ui
	 * @return The {@link MessageEmbed} to display
	 */
	@NotNull
	default MessageEmbed buildEmbed(@NotNull DataState state) {
		var embed = createEmbed(state);

		var entries = getEntries(state);

		state.setCache("size", entries.size());
		state.setCache("maxpage", (entries.size() - 1) / entriesPerPage() + 1);

		int page = state.getState("page");

		if(!entries.isEmpty()) {
			for(int i = ((page - 1) * entriesPerPage()); i < (page * entriesPerPage()) && i < entries.size(); i++) {
				embed.appendDescription(entries.get(i).build(i) + "\n");
			}
		}

		return embed.build();
	}
}
