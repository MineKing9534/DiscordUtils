package de.mineking.discordutils.list;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public record ListContext(ListManager<?> manager, IReplyCallback event) {
}
