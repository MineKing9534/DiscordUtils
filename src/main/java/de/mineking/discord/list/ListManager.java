package de.mineking.discord.list;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ListManager extends Module {
	private final Map<Long, ListState<?>> states = new HashMap<>();
	private Function<ButtonInteractionEvent, Optional<ListState<?>>> customProvider;

	public ListManager(DiscordUtils manager) {
		super(manager);
	}

	public ListManager setCustomProvider(Function<ButtonInteractionEvent, Optional<ListState<?>>> provider) {
		this.customProvider = provider;
		return this;
	}

	public ListManager registerList(long message, ListState<?> state) {
		states.put(message, state);

		return this;
	}

	public void removeList(long message) {
		states.remove(message);
	}

	public ListState<?> getState(long message) {
		return states.get(message);
	}

	public void sendList(IReplyCallback event, int page, Listable<?> listable) {
		var maxPages = listable.getPageCount();
		var state = new ListState<>(Math.min(page, maxPages), listable);

		event.getHook().editOriginal(state.buildMessage(manager, event))
				.queue(message -> {
					event.getHook().deleteOriginal().queueAfter(10, TimeUnit.MINUTES,
							x -> removeList(message.getIdLong()),
							new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE).andThen(e -> removeList(message.getIdLong()))
					);

					registerList(message.getIdLong(), state);
				});
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		if(!event.getComponentId().startsWith("list:")) {
			return;
		}

		Optional.ofNullable((ListState) states.get(event.getMessageIdLong()))
				.or(() -> Optional.ofNullable(customProvider).flatMap(provider -> provider.apply(event)))
				.ifPresent(state -> {
					switch(event.getComponentId().split(":")[1]) {
						case "first" -> state.page = 1;
						case "back" -> state.page--;
						case "next" -> state.page++;
						case "last" -> state.page = state.object.getPageCount();
						default -> {
							return;
						}
					}

					event.editMessage(state.buildMessage(manager, event)).queue();
				});
	}
}
