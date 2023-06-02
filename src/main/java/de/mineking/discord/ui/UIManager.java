package de.mineking.discord.ui;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class UIManager extends Module {
	public final static int MENU_ID_LENGTH = 75;

	private final static Random random = new Random();
	final Map<String, Menu> menus = new HashMap<>();

	public UIManager(DiscordUtils manager) {
		super(manager);
	}

	private String generateId() {
		var temp = new byte[MENU_ID_LENGTH];
		String result;

		do {
			random.nextBytes(temp);
			result = new String(temp);
		} while(menus.containsKey(result));

		return result;
	}

	public synchronized Menu createMenu() {
		var id = generateId();
		var menu = new Menu(this, id);

		menus.put(id, menu);

		return menu;
	}

	public synchronized CompletableFuture<ModalInteractionEvent> waitForModal(IModalCallback event, Function<String, Modal> creator) {
		var id = generateId();

		event.replyModal(creator.apply(id)).queue();

		menus.put(id, null);

		return manager.getEventManager().waitForEvent(ModalInteractionEvent.class, evt -> evt.getModalId().equals(id), Duration.ofMinutes(5)).whenComplete((x, e) -> menus.remove(id));
	}
}
