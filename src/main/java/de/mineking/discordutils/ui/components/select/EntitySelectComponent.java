package de.mineking.discordutils.ui.components.select;

import de.mineking.discordutils.events.IEventHandler;
import de.mineking.discordutils.events.handlers.FilteredEventHandler;
import de.mineking.discordutils.ui.Menu;
import de.mineking.discordutils.ui.components.types.Component;
import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.UpdateState;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class EntitySelectComponent extends Component<EntitySelectInteractionEvent> {
	private final List<EntitySelectMenu.SelectTarget> targets;
	private Function<DataState, String> placeholder = state -> null;
	private Function<DataState, Integer> minValues = state -> null;
	private Function<DataState, Integer> maxValues = state -> null;
	private Predicate<DataState> disabled = state -> false;

	private BiConsumer<UpdateState, Mentions> handler = (state, options) -> {};

	public EntitySelectComponent(@NotNull String name, @NotNull List<EntitySelectMenu.SelectTarget> targets) {
		super(name);

		Checks.notNull(targets, "targets");

		this.targets = targets;
	}

	public EntitySelectComponent(@NotNull String name, @NotNull EntitySelectMenu.SelectTarget... targets) {
		this(name, Arrays.asList(targets));
	}

	@Override
	public int requiredSpace() {
		return 5;
	}

	/**
	 * @param handler The handler to append. Will be called after the currently existing handlers
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent appendHandler(@NotNull BiConsumer<UpdateState, Mentions> handler) {
		Checks.notNull(handler, "handler");

		var temp = this.handler;
		this.handler = (state, options) -> {
			temp.accept(state, options);
			handler.accept(state, options);
		};

		return this;
	}

	/**
	 * @param handler The handler to prepend. Will be called before the currently existing handlers
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent prependHandler(@NotNull BiConsumer<UpdateState, Mentions> handler) {
		Checks.notNull(handler, "handler");

		var temp = this.handler;
		this.handler = (state, options) -> {
			handler.accept(state, options);
			temp.accept(state, options);
		};

		return this;
	}

	/**
	 * @param disabled A function to specify whether the component should be disabled based on the current {@link DataState}
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent asDisabled(@NotNull Predicate<DataState> disabled) {
		Checks.notNull(disabled, "disabled");
		this.disabled = disabled;
		return this;
	}

	/**
	 * @param disabled Whether this component should be disabled
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent asDisabled(boolean disabled) {
		return asDisabled(state -> disabled);
	}

	/**
	 * @param placeholder A function to provide the placeholder for the current {@link DataState}
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent setPlaceholder(@NotNull Function<DataState, String> placeholder) {
		Checks.notNull(placeholder, "placeholder");
		this.placeholder = placeholder;
		return this;
	}

	/**
	 * @param placeholder The placeholder
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent setPlaceholder(@Nullable String placeholder) {
		return setPlaceholder(state -> placeholder);
	}

	/**
	 * @param minValues A function to specify the minimum required number of options for the current {@link DataState}
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent setMinValues(@NotNull Function<DataState, Integer> minValues) {
		Checks.notNull(minValues, "minValues");
		this.minValues = minValues;
		return this;
	}

	/**
	 * @param minValues The minimum required number of options
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent setMinValues(@Nullable Integer minValues) {
		return setMinValues(state -> minValues);
	}

	/**
	 * @param maxValues A function to specify the maximum allowed number of options for the current {@link DataState}
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent setMaxValues(@NotNull Function<DataState, Integer> maxValues) {
		Checks.notNull(maxValues, "maxValues");
		this.maxValues = maxValues;
		return this;
	}

	/**
	 * @param maxValues The maximum allowed number of options
	 * @return {@code this}
	 */
	@NotNull
	public EntitySelectComponent setMaxValues(@Nullable Integer maxValues) {
		return setMaxValues(state -> maxValues);
	}

	@Nullable
	@Override
	public IEventHandler<EntitySelectInteractionEvent> createHandler(@NotNull Menu menu, @NotNull Predicate<EntitySelectInteractionEvent> filter) {
		return new FilteredEventHandler<>(EntitySelectInteractionEvent.class, filter) {
			@Override
			public synchronized void handleEvent(EntitySelectInteractionEvent event) {
				handler.accept(UpdateState.load(menu, event), event.getMentions());
			}
		};
	}

	@NotNull
	@Override
	public ActionComponent build(@NotNull String id, @NotNull UpdateState state) {
		var temp = EntitySelectMenu.create(id, targets)
				.setPlaceholder(placeholder.apply(state))
				.setDisabled(disabled.test(state));

		var minValues = this.minValues.apply(state);
		var maxValues = this.maxValues.apply(state);

		if(minValues != null) temp.setMinValues(minValues);
		if(maxValues != null) temp.setMaxValues(maxValues);

		return temp.build();
	}
}
