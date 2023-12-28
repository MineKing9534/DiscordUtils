package de.mineking.discordutils.ui;

import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.SendState;
import de.mineking.discordutils.ui.state.State;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Menu {
	@NotNull
	public final UIManager manager;

	@NotNull
	public final String id;

	@SuppressWarnings("rawtypes")
	private final Map<String, EffectHandler> effect = new HashMap<>();
	@SuppressWarnings("rawtypes")
	private final Set<EffectHandler> genericEffect = new HashSet<>();


	public Menu(@NotNull UIManager manager, @NotNull String id) {
		this.manager = manager;
		this.id = id;
	}

	/**
	 * <i>Internal method</i>
	 */
	@SuppressWarnings("unchecked")
	public <T> void triggerEffect(@NotNull State<?> state, @NotNull String name, @Nullable T oldValue, @Nullable T newValue) {
		if(effect.containsKey(name)) effect.get(name).handle(state, name, oldValue, newValue);
		genericEffect.forEach(h -> h.handle(state, name, oldValue, newValue));
	}

	/**
	 * <i>Internal method</i>
	 */
	public abstract <T extends Menu> DataState<T> initialize(@NotNull DataState<T> state);

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
	public abstract SendState<?> createState(@Nullable State<?> state);

	/**
	 * @return An {@link SendState} to initialize the state before the first render
	 */
	@NotNull
	public <T extends Menu> SendState<?> createState() {
		return createState(null);
	}

	/**
	 * @param event An {@link IReplyCallback} to reply the menu to
	 */
	public void display(@NotNull GenericComponentInteractionCreateEvent event) {
		createState().display(event);
	}
}
