package de.mineking.discordutils.ui;

import com.google.gson.JsonParser;
import de.mineking.discordutils.ui.components.types.ComponentRow;
import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.SendState;
import de.mineking.discordutils.ui.state.State;
import de.mineking.discordutils.ui.state.UpdateState;
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

import java.util.*;
import java.util.function.Function;

public class Menu {
	@NotNull
	public final UIManager manager;

	@NotNull
	public final String id;

	@NotNull
	public final Function<DataState, MessageEmbed> embed;

	@NotNull
	public final List<ComponentRow> components;

	@SuppressWarnings("rawtypes")
	private final Map<String, EffectHandler> effect = new HashMap<>();
	@SuppressWarnings("rawtypes")
	private final Set<EffectHandler> genericEffect = new HashSet<>();

	Menu(@NotNull UIManager manager, @NotNull String id, @NotNull Function<DataState, MessageEmbed> embed, @NotNull List<ComponentRow> components) {
		this.manager = manager;
		this.id = id;
		this.embed = embed;
		this.components = components;
	}

	/**
	 * @param state The {@link UpdateState}
	 * @return The rendered message for the provided state
	 */
	@NotNull
	public MessageEditData buildMessage(@NotNull UpdateState state) {
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

	/**
	 * <i>Internal method</i>
	 */
	@SuppressWarnings("unchecked")
	public <T> void triggerEffect(@NotNull State state, @NotNull String name, @Nullable T oldValue, @Nullable T newValue) {
		if(effect.containsKey(name)) effect.get(name).handle(state, name, oldValue, newValue);
		genericEffect.forEach(h -> h.handle(state, name, oldValue, newValue));
	}

	/**
	 * @param name    The name of the state to listen to
	 * @param handler An {@link EffectHandler} that is called every time the provided state changes
	 * @return {@code this}
	 */
	@NotNull
	public <T> Menu effect(@NotNull String name, @NotNull EffectHandler<T> handler) {
		Checks.notNull(name, "name");
		Checks.notNull(handler, "handler");

		effect.put(name, handler);
		return this;
	}

	/**
	 * @param handler An {@link EffectHandler} that is called every time a state changes
	 * @return {@code this}
	 */
	@NotNull
	public Menu effect(@NotNull EffectHandler<?> handler) {
		Checks.notNull(handler, "handler");
		genericEffect.add(handler);
		return this;
	}

	/**
	 * @param state An existing state to copy the values from
	 * @return An {@link SendState} to initialize the state before the first render
	 */
	@NotNull
	public SendState createState(@Nullable State state) {
		return new SendState(this, state == null ? JsonParser.parseString("{}").getAsJsonObject() : state.data);
	}

	/**
	 * @return An {@link SendState} to initialize the state before the first render
	 */
	@NotNull
	public SendState createState() {
		return createState(null);
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
