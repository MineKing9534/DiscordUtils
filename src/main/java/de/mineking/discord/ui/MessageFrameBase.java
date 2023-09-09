package de.mineking.discord.ui;

import de.mineking.discord.ui.components.ComponentRow;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class MessageFrameBase extends MenuFrame {
	protected final Set<CompletableFuture<?>> futures = new HashSet<>();

	public MessageFrameBase(Menu menu) {
		super(menu);
	}

	public abstract MessageEmbed getEmbed();
	public abstract Collection<ComponentRow> getComponents();

	public MessageEditBuilder buildMessage() {
		return new MessageEditBuilder()
				.setEmbeds(getEmbed())
				.setComponents(getComponents().stream().map(c -> c.build(menu)).toList());
	}

	@Override
	public void render() {
		if(menu.state.reply == null) throw new IllegalStateException();

		var message = buildMessage().build();

		if(menu.state.reply.isAcknowledged()) menu.state.reply.getHook().editOriginal(message).queue();
		else if(menu.state.reply instanceof IMessageEditCallback edit) edit.editMessage(message).queue();
		else menu.state.reply.reply(MessageCreateData.fromEditData(message)).setEphemeral(true).queue();

		futures.addAll(
				getComponents().stream()
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
