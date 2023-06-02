package de.mineking.discord.list;

import de.mineking.discord.DiscordUtils;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.List;

public class ListContext<T extends ListEntry> {
	public final DiscordUtils manager;
	public final IReplyCallback event;
	public final int page;
	public final List<T> entries;

	public ListContext(DiscordUtils manager, IReplyCallback event, int page, List<T> entries) {
		this.manager = manager;
		this.event = event;
		this.page = page;
		this.entries = entries;
	}
}
