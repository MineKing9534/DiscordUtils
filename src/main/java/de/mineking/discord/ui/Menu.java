package de.mineking.discord.ui;

import de.mineking.discord.events.EventManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.time.Duration;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Menu implements MenuBase {
	public final static Duration timeout = Duration.ofMinutes(10);

	final UIManager manager;
	final String id;

	private MenuFrame current;

	CallbackState state;
	private final HashMap<String, MenuFrame> frames = new HashMap<>();

	Menu(UIManager manager, String id) {
		this.manager = manager;
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public EventManager getEventManager() {
		return manager.getManager().getEventManager();
	}

	@Override
	public void handle(GenericComponentInteractionCreateEvent event) {
		state.reply = event;
		state.modal = event;
	}

	void update(IReplyCallback reply, IModalCallback modal) {
		state.reply = reply;
		state.modal = modal;
	}

	public Menu addMessageFrame(String name, Supplier<MessageEmbed> message, Consumer<MessageFrame> config) {
		var frame = new MessageFrame(this, message);
		config.accept(frame);
		frames.put(name, frame);
		return this;
	}

	public Menu addModalFrame(String name, String title, Consumer<Modal.Builder> config, BiConsumer<Menu, ModalInteractionEvent> handler) {
		var builder = Modal.create(id + ":" + name, title);
		config.accept(builder);
		var frame = new ModalFrame(this, builder.build(), handler);
		frames.put(name, frame);
		return this;
	}

	private void showFrame(MenuFrame frame) {
		try {
			if(current != null) {
				current.cleanup();
			}

			current = frame;
			frame.show();
		} catch(Exception e) {
			close();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update() {
		if(current == null) {
			throw new IllegalStateException();
		}

		showFrame(current);
	}

	@Override
	public void display(String name) {
		if(state == null) {
			throw new IllegalStateException();
		}

		showFrame(frames.get(name));
	}

	public void start(CallbackState state, String name) {
		if(this.state != null) {
			throw new IllegalStateException();
		}

		this.state = state;
		display(name);
	}

	@Override
	public void close() {
		if(current != null) {
			current.cleanup();
		}

		manager.menus.remove(id);
	}
}
