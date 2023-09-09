package de.mineking.discord.ui;

import de.mineking.discord.events.EventManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.*;

public class Menu implements MenuBase {
	public final static Logger logger = LoggerFactory.getLogger(Menu.class);
	public final static Duration timeout = Duration.ofMinutes(10);

	protected final UIManager manager;
	protected final String id;

	protected final List<MenuFrame> current = new LinkedList<>();

	protected CallbackState state;
	protected final HashMap<String, MenuFrame> frames = new HashMap<>();

	public final Map<String, Object> data = new HashMap<>();

	protected Menu(UIManager manager, String id) {
		this.manager = manager;
		this.id = id;
	}

	@Override
	public <T> void putData(String name, T object) {
		data.put(name, object);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getData(String name, Class<T> type) {
		return (T) data.get(name);
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
	public void handle(GenericInteractionCreateEvent event) {
		state.reply = event instanceof IReplyCallback reply ? reply : null;
		state.modal = event instanceof IModalCallback modal ? modal : null;
	}

	public Menu addMessageFrame(String name, Supplier<MessageEmbed> message, Consumer<MessageFrame> config) {
		var frame = new MessageFrame(this, message);
		config.accept(frame);
		return addFrame(name, frame);
	}

	public Menu addModalFrame(String name, String title, Consumer<Modal.Builder> config, BiConsumer<Menu, ModalInteractionEvent> handler) {
		var frame = new ModalFrame(this, () -> {
			var builder = Modal.create(id + ":" + name, title);
			config.accept(builder);
			return builder.build();
		}, handler);
		return addFrame(name, frame);
	}

	public Menu addFrame(String name, MenuFrame frame) {
		frames.put(name, frame);
		return this;
	}

	public Menu addFrame(String name, Function<Menu, ? extends MenuFrame> frame) {
		return addFrame(name, frame.apply(this));
	}

	public <T> Menu addFrame(String name, BiFunction<Menu, T, ? extends MenuFrame> frame, T param) {
		return addFrame(name, frame.apply(this, param));
	}

	private void showFrame(MenuFrame frame) {
		if(frame == null) throw new IllegalArgumentException("frame may not be null");

		try {
			if(frame instanceof ModalFrame && !current.isEmpty()) {
				var last = current.get(current.size() - 1);
				current.forEach(MenuFrame::cleanup);
				current.clear();
				current.add(last);
			} else {
				current.forEach(MenuFrame::cleanup);
				current.clear();
			}

			current.add(0, frame);
			current.forEach(MenuFrame::render);
		} catch(Exception e) {
			logger.error("An error occurred whilst rendering frame", e);
			close();
		}
	}

	@Override
	public void update() {
		if(current.isEmpty()) throw new IllegalStateException();
		showFrame(current.get(0));
	}

	@Override
	public void display(String name) {
		if(state == null) throw new IllegalStateException();
		if(!frames.containsKey(name)) throw new IllegalArgumentException("No frame with the name '" + name + "' found");

		showFrame(frames.get(name));
	}

	public void start(CallbackState state, String name) {
		if(this.state != null) throw new IllegalStateException();

		this.state = state;
		display(name);
	}

	@Override
	public void close(boolean delete) {
		if(state.reply != null && delete) {
			if(!state.reply.isAcknowledged() && state.reply instanceof IMessageEditCallback edit) {
				edit.deferEdit().queue();
			}

			state.reply.getHook().deleteOriginal().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE, ErrorResponse.UNKNOWN_INTERACTION));
		}

		current.forEach(MenuFrame::cleanup);

		manager.menus.remove(id);
	}

	@Override
	public CallbackState getState() {
		return state;
	}
}
