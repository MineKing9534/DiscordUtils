package de.mineking.discord.ui;

import de.mineking.discord.ui.components.ComponentRow;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MessageFrame extends MenuFrame {
	private final Supplier<MessageEmbed> message;
	private final List<ComponentRow> components = new ArrayList<>();

	private final Set<CompletableFuture<?>> futures = new HashSet<>();

	public MessageFrame(Menu menu, Supplier<MessageEmbed> message) {
		super(menu);

		this.message = message;
	}

	public MessageFrame addComponents(ComponentRow... rows) {
		return addComponents(Arrays.asList(rows));
	}

	public MessageFrame addComponents(Collection<ComponentRow> rows) {
		components.addAll(rows);

		return this;
	}

	private MessageEditData buildMessage() {
		return new MessageEditBuilder()
				.setEmbeds(message.get())
				.setComponents(components.stream().map(c -> c.build(menu)).toList())
				.build();
	}

	@Override
	public void show() {
		if(menu.state.reply == null) {
			throw new IllegalStateException();
		}

		var message = buildMessage();

		if(menu.state.reply.isAcknowledged()) menu.state.reply.getHook().editOriginal(message).queue();
		else if(menu.state.reply instanceof IMessageEditCallback edit) edit.editMessage(message).queue();
		else menu.state.reply.reply(MessageCreateData.fromEditData(message)).setEphemeral(true).queue();

		futures.addAll(
				components.stream()
						.flatMap(c -> c.getComponents().stream())
						.map(c -> c.createHandler(menu))
						.toList()
		);
	}

	@Override
	public void cleanup() {
		futures.forEach(f -> f.cancel(true));
		futures.clear();
	}
}
