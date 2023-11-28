package de.mineking.discordutils.ui;

import com.google.gson.JsonParser;
import de.mineking.discordutils.ui.components.types.ComponentRow;
import de.mineking.discordutils.ui.state.*;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class MessageMenu extends Menu {
	@NotNull
	public final Function<DataState<MessageMenu>, MessageEmbed> embed;

	@NotNull
	public final List<ComponentRow> components;

	MessageMenu(@NotNull UIManager manager, @NotNull String id, @NotNull Function<DataState<MessageMenu>, MessageEmbed> embed, @NotNull List<ComponentRow> components) {
		super(manager, id);

		this.embed = embed;
		this.components = components;
	}

	/**
	 * @param state The {@link UpdateState}
	 * @return The rendered message for the provided state
	 */
	@NotNull
	public MessageEditData buildMessage(@NotNull UpdateState state) {
		Checks.notNull(state, "state");

		var data = new StringBuilder(state.data.toString());

		var temp = new MessageEditBuilder()
				.setEmbeds(embed.apply(state))
				.setComponents(
						this.components.stream()
								.map(r -> ActionRow.of(
										r.getComponents().stream()
												.map(c -> {
													var id = this.id + ":" + c.name + ":";

													if(!data.isEmpty()) {
														var pos = Math.min(Button.ID_MAX_LENGTH - id.length(), data.length());
														id += data.substring(0, pos);
														data.delete(0, pos);
													}

													return c.build(id, state);
												})
												.toList()
								))
								.toList()
				)
				.build();

		if(!data.isEmpty()) throw new IllegalStateException("State is too large. Either add more components to give more space or shrink your state size: [%d] %s, left: [%d] %s".formatted(
				state.data.toString().length(),
				state.data.toString(),
				data.length(),
				data.toString()
		));

		return temp;
	}

	@NotNull
	@Override
	public MessageMenu effect(@NotNull EffectHandler<?> handler) {
		return (MessageMenu) super.effect(handler);
	}

	@NotNull
	@Override
	public <T> MessageMenu effect(@NotNull String name, @NotNull EffectHandler<T> handler) {
		return (MessageMenu) super.effect(name, handler);
	}

	@NotNull
	@Override
	public MessageSendState createState(@Nullable State<?> state) {
		return new MessageSendState(this, state == null ? JsonParser.parseString("{}").getAsJsonObject() : state.data);
	}

	@NotNull
	@Override
	public MessageSendState createState() {
		return (MessageSendState) super.createState();
	}

	/**
	 * @param channel The channel to send the menu in
	 */
	public void display(@NotNull MessageChannel channel) {
		createState().display(channel);
	}

	/**
	 * @param event     An {@link IReplyCallback} to reply the menu to
	 * @param ephemeral Whether the message should only be visible to the user who created the interaction
	 */
	public void display(@NotNull IReplyCallback event, boolean ephemeral) {
		createState().display(event, ephemeral);
	}

	/**
	 * @param event An {@link IReplyCallback} to reply the menu to
	 */
	public void display(@NotNull IReplyCallback event) {
		display(event, true);
	}
}
