package de.mineking.discord.ui;

import de.mineking.discord.ui.components.ComponentRow;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class MessageFrame extends MessageFrameBase {
	private final Function<Menu, MessageEmbed> message;
	private final List<ComponentRow> components = new ArrayList<>();

	private final Set<CompletableFuture<?>> futures = new HashSet<>();

	public MessageFrame(Menu menu, Function<Menu, MessageEmbed> message) {
		super(menu);
		this.message = message;
	}

	public MessageFrame(Menu menu, Supplier<MessageEmbed> message) {
		this(menu, m -> message.get());
	}

	public MessageFrame addComponents(ComponentRow... rows) {
		return addComponents(Arrays.asList(rows));
	}

	public MessageFrame addComponents(Collection<ComponentRow> rows) {
		components.addAll(rows);

		return this;
	}

	@Override
	public MessageEmbed getEmbed() {
		return message.apply(menu);
	}

	@Override
	public List<ComponentRow> getComponents() {
		return components;
	}
}
