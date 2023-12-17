package de.mineking.discordutils.list;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ListContext<T extends ListEntry>(@NotNull ListManager<?> manager, @NotNull IReplyCallback event, @NotNull List<T> entries) {
}
