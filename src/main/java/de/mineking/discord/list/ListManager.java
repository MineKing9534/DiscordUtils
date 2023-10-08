package de.mineking.discord.list;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import de.mineking.discord.ui.UIManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class ListManager extends Module {
	private final Map<Long, ListState<?>> states = new HashMap<>();
	private BiFunction<Long, IReplyCallback, Optional<ListState<?>>> customProvider;

	public ListManager(DiscordUtils manager) {
		super(manager);
	}

	public ListManager setCustomProvider(BiFunction<Long, IReplyCallback, Optional<ListState<?>>> provider) {
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

	public Optional<ListState<?>> getState(long message) {
		return Optional.ofNullable(states.get(message));
	}

	@SuppressWarnings("rawtypes")
	public Optional<ListState> getState(long message, IReplyCallback event) {
		return Optional.ofNullable((ListState) states.get(message))
				.or(() -> Optional.ofNullable(customProvider).flatMap(provider -> provider.apply(message, event)));
	}

	public void sendList(IReplyCallback event, int page, Listable<?> listable, Map<String, Object> data) {
		var maxPages = listable.getPageCount(new ListContext<>(manager, event, -1, data, Collections.emptyList()));
		var state = new ListState<>(Math.min(page, maxPages), listable, data);

		event.getHook().editOriginal(state.buildMessage(manager, event))
				.queue(message -> {
					event.getHook().deleteOriginal().queueAfter(10, TimeUnit.MINUTES,
							x -> removeList(message.getIdLong()),
							new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE).andThen(e -> removeList(message.getIdLong()))
					);

					registerList(message.getIdLong(), state);
				});
	}

	public void sendList(IReplyCallback event, int page, Listable<?> listable) {
		sendList(event, page, listable, Collections.emptyMap());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		if(!event.getComponentId().startsWith("list:")) {
			return;
		}

		getState(event.getMessageIdLong(), event).ifPresentOrElse(
				state -> {
					switch(event.getComponentId().split(":")[1]) {
						case "first" -> state.page = 1;
						case "back" -> state.page--;
						case "next" -> state.page++;
						case "last" -> state.page = state.object.getPageCount(state.createContext(manager, event));
						default -> {
							return;
						}
					}

					event.editMessage(state.buildMessage(manager, event)).queue();
				},
				() -> manager.getModule(UIManager.class).ifPresent(ui -> ui.sendDefault(event))
		);
	}
}
