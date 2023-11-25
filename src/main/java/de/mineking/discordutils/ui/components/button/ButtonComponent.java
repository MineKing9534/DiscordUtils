package de.mineking.discordutils.ui.components.button;

import de.mineking.discordutils.events.IEventHandler;
import de.mineking.discordutils.events.handlers.FilteredEventHandler;
import de.mineking.discordutils.ui.Menu;
import de.mineking.discordutils.ui.components.types.Component;
import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.UpdateState;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ButtonComponent extends Component<ButtonInteractionEvent> {
	private final static ScheduledExecutorService executor = Executors.newScheduledThreadPool(0);
	private final LabelProvider label;
	private final Function<DataState, ButtonColor> color;
	private Predicate<DataState> disabled = state -> false;

	private Consumer<UpdateState> handler = state -> {};
	private Consumer<UpdateState> doubleClick = null;
	private int doubleClickTimeout = 3;

	private Future<?> future;

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color A function to get the {@link ButtonColor} for the current {@link DataState}
	 * @param label The label to use
	 */
	public ButtonComponent(@NotNull String name, @NotNull Function<DataState, ButtonColor> color, @NotNull LabelProvider label) {
		super(name);

		Checks.notNull(color, "color");
		Checks.notNull(label, "label");

		this.color = color;
		this.label = label;
	}

	@Override
	public int requiredSpace() {
		return 1;
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color A function to get the {@link ButtonColor} for the current {@link DataState}
	 * @param label The label to use
	 */
	public ButtonComponent(@NotNull String name, @NotNull Function<DataState, ButtonColor> color, @NotNull String label) {
		this(name, color, new LabelProvider() {
			@Override
			public String getText(@NotNull DataState state) {
				return label;
			}
		});
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color A function to get the {@link ButtonColor} for the current {@link DataState}
	 * @param label The label to use
	 */
	public ButtonComponent(@NotNull String name, @NotNull Function<DataState, ButtonColor> color, @NotNull Emoji label) {
		this(name, color, new LabelProvider() {
			@Override
			public Emoji getEmoji(@NotNull DataState state) {
				return label;
			}
		});
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public ButtonComponent(@NotNull String name, @NotNull ButtonColor color, @NotNull LabelProvider label) {
		this(name, state -> color, label);
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public ButtonComponent(@NotNull String name, @NotNull ButtonColor color, @NotNull String label) {
		this(name, state -> color, new LabelProvider() {
			@Override
			public String getText(@NotNull DataState state) {
				return label;
			}
		});
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public ButtonComponent(@NotNull String name, @NotNull ButtonColor color, @NotNull Emoji label) {
		this(name, state -> color, new LabelProvider() {
			@Override
			public Emoji getEmoji(@NotNull DataState state) {
				return label;
			}
		});
	}

	/**
	 * @param handler The handler to append. Will be called after the currently existing handlers
	 * @return {@code this}
	 */
	@NotNull
	public ButtonComponent appendHandler(@NotNull Consumer<UpdateState> handler) {
		Checks.notNull(handler, "handler");

		var temp = this.handler;
		this.handler = state -> {
			temp.accept(state);
			handler.accept(state);
		};

		return this;
	}

	/**
	 * @param handler The handler to prepend. Will be called before the currently existing handlers
	 * @return {@code this}
	 */
	@NotNull
	public ButtonComponent prependHandler(@NotNull Consumer<UpdateState> handler) {
		Checks.notNull(handler, "handler");

		var temp = this.handler;
		this.handler = state -> {
			handler.accept(state);
			temp.accept(state);
		};

		return this;
	}

	/**
	 * @param handler The double click handler to append. When a double click handler is provided, all click actions will be delayed to be able to detect double clicks
	 * @return {@code this}
	 */
	@NotNull
	public ButtonComponent appendDoubleclickHandler(@NotNull Consumer<UpdateState> handler) {
		Checks.notNull(handler, "handler");

		var temp = this.doubleClick;
		this.doubleClick = state -> {
			if(temp != null) temp.accept(state);
			handler.accept(state);
		};

		return this;
	}

	/**
	 * @param handler The double click handler to append. When a double click handler is provided, all click actions will be delayed to be able to detect double clicks
	 * @return {@code this}
	 */
	@NotNull
	public ButtonComponent prependDoubleclickHandler(@NotNull Consumer<UpdateState> handler) {
		Checks.notNull(handler, "handler");

		var temp = this.doubleClick;
		this.doubleClick = state -> {
			handler.accept(state);
			if(temp != null) temp.accept(state);
		};

		return this;
	}

	/**
	 * @param seconds The delay between clicks to be detected as double click
	 * @return {@code this}
	 * @see #appendDoubleclickHandler(Consumer)
	 */
	@NotNull
	public ButtonComponent setDoubleclickTimeout(int seconds) {
		this.doubleClickTimeout = seconds;
		return this;
	}

	/**
	 * @param disabled A function to specify whether the component should be disabled based on the current {@link DataState}
	 * @return {@code this}
	 */
	@NotNull
	public ButtonComponent asDisabled(@NotNull Predicate<DataState> disabled) {
		Checks.notNull(disabled, "disabled");
		this.disabled = disabled;
		return this;
	}

	/**
	 * @param disabled Whether this component should be disabled
	 * @return {@code this}
	 */
	@NotNull
	public ButtonComponent asDisabled(boolean disabled) {
		return asDisabled(state -> disabled);
	}

	@Nullable
	@Override
	public IEventHandler<ButtonInteractionEvent> createHandler(@NotNull Menu menu, @NotNull Predicate<ButtonInteractionEvent> filter) {
		return new FilteredEventHandler<>(ButtonInteractionEvent.class, filter) {
			@Override
			public synchronized void handleEvent(ButtonInteractionEvent event) {
				var state = UpdateState.load(menu, event);

				if(doubleClick == null) handler.accept(state);
				else {
					if(future != null && !future.isDone()) {
						future.cancel(true);
						future = null;

						doubleClick.accept(state);
					} else {
						event.deferEdit().queue();
						future = executor.schedule(() -> handler.accept(state), doubleClickTimeout, TimeUnit.SECONDS);
					}
				}
			}
		};
	}

	@NotNull
	@Override
	public ActionComponent build(@NotNull String id, @NotNull UpdateState state) {
		var text = label.getText(state);
		var emoji = label.getEmoji(state);
		var color = this.color.apply(state);

		var button = emoji != null
				? Button.of(color.style, id, emoji)
				: Button.of(color.style, id, Objects.requireNonNullElse(text, name));

		return button.withDisabled(disabled.test(state));
	}
}
