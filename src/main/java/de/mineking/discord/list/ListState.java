package de.mineking.discord.list;

import de.mineking.discord.DiscordUtils;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;

public class ListState<T extends ListEntry> {
	public final Listable<T> object;
	public int page;

	public ListState(int page, Listable<T> object) {
		this.page = page;
		this.object = object;
	}

	public MessageEditData buildMessage(DiscordUtils manager, IReplyCallback event) {
		return object.buildMessage(page, new ListContext<>(manager, event, page, object.getEntries(event)));
	}
}
