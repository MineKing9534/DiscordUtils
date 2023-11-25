package de.mineking.discordutils.list;

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
	List<T> getEntries(@NotNull DataState state, @NotNull ListContext context);

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
	default EmbedBuilder createEmbed(@NotNull DataState state, @NotNull ListContext context) {
		return new EmbedBuilder();
	}

	/**
	 * @param state   The {@link DataState} holding information about the current state of the list ui
	 * @param context The {@link ListContext}
	 * @return The {@link MessageEmbed} to display
	 */
	@NotNull
	default MessageEmbed buildEmbed(@NotNull DataState state, @NotNull ListContext context) {
		var embed = createEmbed(state, context);

		var entries = getEntries(state, context);

		state.setCache("size", entries.size());

		var maxPage = (entries.size() - 1) / entriesPerPage() + 1;
		state.setCache("maxpage", maxPage);

		int page = Math.max(Math.min(state.<Integer>getOptionalState("page").orElse(1), 1), maxPage);

		if(!entries.isEmpty()) {
			for(int i = ((page - 1) * entriesPerPage()); i < (page * entriesPerPage()) && i < entries.size(); i++) {
				embed.appendDescription(entries.get(i).build(i, context) + "\n");
			}
		}

		return embed.build();
	}
}
