package de.mineking.discord.list;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collections;
import java.util.List;

public interface Listable<T extends ListEntry> {
	int defaultEntriesPerPage = 20;

	List<T> getEntries(ListContext<T> context);

	default int entriesPerPage() {
		return defaultEntriesPerPage;
	}

	default int getPageCount(ListContext<T> context) {
		return (getEntries(context).size() - 1) / entriesPerPage() + 1;
	}

	default MessageEditData buildMessage(ListContext<T> context) {
		var embed = createEmbed(context);

		applyEntries(embed, context);

		return new MessageEditBuilder()
				.setEmbeds(embed.build())
				.setComponents(getComponents(context))
				.build();
	}

	default List<ActionRow> getComponents(ListContext<T> context) {
		var pageCount = getPageCount(context);

		return Collections.singletonList(ActionRow.of(
				Button.secondary("list:first", Emoji.fromUnicode("⏪")).withDisabled(context.page == 1),
				Button.secondary("list:back", Emoji.fromUnicode("⬅")).withDisabled(context.page <= 1),
				Button.secondary("page", "📖 " + context.page + "/" + pageCount).asDisabled(),
				Button.secondary("list:next", Emoji.fromUnicode("➡")).withDisabled(context.page >= pageCount),
				Button.secondary("list:last", Emoji.fromUnicode("⏩")).withDisabled(context.page == pageCount)
		));
	}

	default EmbedBuilder createEmbed(ListContext<T> context) {
		return new EmbedBuilder();
	}

	default void applyEntries(EmbedBuilder builder, ListContext<T> context) {
		if(!context.entries.isEmpty()) {
			for(int i = ((context.page - 1) * entriesPerPage()); i < (context.page * entriesPerPage()) && i < context.entries.size(); i++) {
				builder.appendDescription(context.entries.get(i).build(i, context) + "\n");
			}
		}
	}
}
