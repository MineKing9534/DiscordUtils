package de.mineking.discord.list;

import de.mineking.discord.DiscordUtils;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;
import java.util.Map;

public class ListState<T extends ListEntry> {
	public Listable<T> object;
	public final Map<String, Object> data;
	public int page;

	public ListState(int page, Listable<T> object, Map<String, Object> data) {
		this.page = page;
		this.data = data;
		this.object = object;
	}

	public ListContext<T> createContext(DiscordUtils manager, IReplyCallback event) {
		var context = new ListContext<T>(manager, event, page, data, new ArrayList<>());
		context.entries.addAll(object.getEntries(context));
		return context;
	}

	public MessageEditData buildMessage(DiscordUtils manager, IReplyCallback event) {
		return object.buildMessage(createContext(manager, event));
	}
}
