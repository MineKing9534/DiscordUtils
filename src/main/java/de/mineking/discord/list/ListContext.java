package de.mineking.discord.list;

import de.mineking.discord.DiscordUtils;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.List;
import java.util.Map;

public class ListContext<T extends ListEntry> {
	public final DiscordUtils manager;
	public final IReplyCallback event;
	public final int page;
	public final Map<String, Object> data;
	public final List<T> entries;

	public ListContext(DiscordUtils manager, IReplyCallback event, int page, Map<String, Object> data, List<T> entries) {
		this.manager = manager;
		this.event = event;
		this.page = page;
		this.data = data;
		this.entries = entries;
	}
}
