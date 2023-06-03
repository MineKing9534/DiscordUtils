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
import java.util.stream.Collectors;

public class UIManager extends Module {
	private final static Random random = new Random();
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	public final static int MENU_ID_LENGTH = 50;

	final Map<String, Menu> menus = new HashMap<>();

	public UIManager(DiscordUtils manager) {
		super(manager);
	}

	private String randomString(int length) {
		return random
				.ints(length, 0, CHARACTERS.length())
				.mapToObj(CHARACTERS::charAt)
				.map(Object::toString)
				.collect(Collectors.joining());
	}

	private String generateId() {
		while(true) {
			var temp = randomString(MENU_ID_LENGTH);

			if(!menus.containsKey(temp)) {
				return temp;
			}
		}
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

		var future = manager.getEventManager().waitForEvent(ModalInteractionEvent.class, evt -> evt.getModalId().equals(id), Duration.ofMinutes(5));
		future.whenComplete((x, e) -> menus.remove(id));
		return future;
	}
}
