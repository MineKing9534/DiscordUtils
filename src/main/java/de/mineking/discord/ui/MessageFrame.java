package de.mineking.discord.ui;

import de.mineking.discord.ui.components.ComponentRow;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class MessageFrame extends MessageFrameBase {
	private final Function<Menu, MessageEmbed> message;
	private final List<Function<Menu, FileUpload>> files = new ArrayList<>();

	private final List<ComponentRow> components = new ArrayList<>();

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

	public MessageFrame addFile(Function<Menu, FileUpload> file) {
		files.add(file);
		return this;
	}

	public MessageFrame addFile(FileUpload file) {
		files.add(m -> file);
		return this;
	}

	@Override
	public MessageEditBuilder buildMessage() {
		return super.buildMessage()
				.setFiles(
						files.stream()
								.map(f -> f.apply(menu))
								.toList()
				);
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
