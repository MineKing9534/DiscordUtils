package de.mineking.discordutils.ui.components.button;

import de.mineking.discordutils.ui.Menu;
import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.components.button.label.EmojiLabel;
import de.mineking.discordutils.ui.components.button.label.LabelProvider;
import de.mineking.discordutils.ui.components.button.label.TextLabel;
import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.SendState;
import de.mineking.discordutils.ui.state.UpdateState;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MenuComponent<T extends Menu> extends ButtonComponent {
	@SuppressWarnings("unchecked")
	private BiFunction<T, UpdateState, SendState<T>> creator = (menu, state) -> (SendState<T>) menu.createState();

	/**
	 * @param menu  The menu to display when clicked
	 * @param color A function to get the {@link ButtonColor} for the current {@link DataState}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull T menu, @NotNull Function<DataState<MessageMenu>, ButtonColor> color, @NotNull LabelProvider label) {
		super(menu.id, color, label);

		appendHandler(state -> state.getEvent().ifPresent(event -> {
			if(event instanceof IMessageEditCallback edit) edit.deferEdit().queue();
			if(event instanceof GenericComponentInteractionCreateEvent ce) creator.apply(menu, state).display(ce);
		}));
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color A function to get the {@link ButtonColor} for the current {@link DataState}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull T menu, @NotNull Function<DataState<MessageMenu>, ButtonColor> color, @NotNull String label) {
		this(menu, color, (TextLabel) state -> label);
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color A function to get the {@link ButtonColor} for the current {@link DataState}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull T menu, @NotNull Function<DataState<MessageMenu>, ButtonColor> color, @NotNull Emoji label) {
		this(menu, color, (EmojiLabel) state -> label);
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull T menu, @NotNull ButtonColor color, @NotNull LabelProvider label) {
		this(menu, state -> color, label);
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull T menu, @NotNull ButtonColor color, @NotNull String label) {
		this(menu, state -> color, (TextLabel) state -> label);
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull T menu, @NotNull ButtonColor color, @NotNull Emoji label) {
		this(menu, state -> color, (EmojiLabel) state -> label);
	}

	/**
	 * @param creator A function to create the new state for the menu. Can be used to set start values
	 * @return {@code this}
	 */
	@NotNull
	public MenuComponent<T> setStateCreator(@NotNull BiFunction<T, UpdateState, SendState<T>> creator) {
		Checks.notNull(creator, "creator");
		this.creator = creator;
		return this;
	}

	/**
	 * Sets a state creator, that automatically copies the current state to the new menu
	 *
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public MenuComponent<T> transfereState() {
		return setStateCreator((menu, state) -> (SendState<T>) menu.createState(state));
	}
}
