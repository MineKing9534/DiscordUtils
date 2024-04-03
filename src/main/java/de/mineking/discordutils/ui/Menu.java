package de.mineking.discordutils.ui;

import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.SendState;
import de.mineking.discordutils.ui.state.State;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public abstract class Menu {
	private final static Logger logger = LoggerFactory.getLogger(Menu.class);

	private final UIManager manager;
	private final String id;

	@SuppressWarnings("rawtypes")
	private final Set<EffectHandler> effect = new HashSet<>();


	public Menu(@NotNull UIManager manager, @NotNull String id) {
		this.manager = manager;
		this.id = id;
	}

	/**
	 * @return The {@link UIManager} for this menu
	 */
	@NotNull
	public UIManager getManager() {
		return manager;
	}

	/**
	 * @return The id of this menu
	 */
	@NotNull
	public String getId() {
		return id;
	}

	/**
	 * <i>Internal method</i>
	 */
	@SuppressWarnings("unchecked")
	public <T> void triggerEffect(@NotNull DataState<?> state, @NotNull String name, @Nullable T oldValue, @Nullable T newValue) {
		effect.forEach(h -> {
			try {
				h.handle(state, name, oldValue, newValue);
			} catch (Exception e) {
				logger.error("Failed to trigger effect handler", e);
			}
		});
	}

	/**
	 * <i>Internal method</i>
	 */
	public abstract void initialize(@NotNull DataState<?> state);

	/**
	 * @param name    The name of the state to listen to
	 * @param handler An {@link EffectHandler} that is called every time the provided state changes
	 * @return {@code this}
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public <T> Menu effect(@NotNull String name, @NotNull EffectHandler<T> handler) {
		Checks.notNull(name, "name");
		Checks.notNull(handler, "handler");

		effect.add((state, n, oldValue, newValue) -> {
			if (!n.equals(name)) return;
			handler.handle(state, n, (T) oldValue, (T) newValue);
		});
		return this;
	}

	/**
	 * @param handler An {@link EffectHandler} that is called every time a state changes
	 * @return {@code this}
	 */
	@NotNull
	public Menu effect(@NotNull EffectHandler<?> handler) {
		Checks.notNull(handler, "handler");
		effect.add(handler);
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
