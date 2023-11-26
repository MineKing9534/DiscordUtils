package de.mineking.discordutils.ui.components.button;

import de.mineking.discordutils.ui.Menu;
import de.mineking.discordutils.ui.components.button.label.EmojiLabel;
import de.mineking.discordutils.ui.components.button.label.LabelProvider;
import de.mineking.discordutils.ui.components.button.label.TextLabel;
import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.SendState;
import de.mineking.discordutils.ui.state.UpdateState;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MenuComponent extends ButtonComponent {
	private BiFunction<Menu, UpdateState, SendState> creator = Menu::createState;

	/**
	 * @param menu  The menu to display when clicked
	 * @param color A function to get the {@link ButtonColor} for the current {@link DataState}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull Menu menu, @NotNull Function<DataState, ButtonColor> color, @NotNull LabelProvider label) {
		super(menu.id, color, label);

		appendHandler(state -> state.getEvent().ifPresent(event -> {
			if(event instanceof IMessageEditCallback edit) edit.deferEdit().queue();
			creator.apply(menu, state).display(event);
		}));
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color A function to get the {@link ButtonColor} for the current {@link DataState}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull Menu menu, @NotNull Function<DataState, ButtonColor> color, @NotNull String label) {
		this(menu, color, (TextLabel) state -> label);
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color A function to get the {@link ButtonColor} for the current {@link DataState}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull Menu menu, @NotNull Function<DataState, ButtonColor> color, @NotNull Emoji label) {
		this(menu, color, (EmojiLabel) state -> label);
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull Menu menu, @NotNull ButtonColor color, @NotNull LabelProvider label) {
		this(menu, state -> color, label);
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull Menu menu, @NotNull ButtonColor color, @NotNull String label) {
		this(menu, state -> color, (TextLabel) state -> label);
	}

	/**
	 * @param menu  The menu to display when clicked
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public MenuComponent(@NotNull Menu menu, @NotNull ButtonColor color, @NotNull Emoji label) {
		this(menu, state -> color, (EmojiLabel) state -> label);
	}

	/**
	 * @param creator A function to create the new state for the menu. Can be used to set start values
	 * @return {@code this}
	 */
	@NotNull
	public MenuComponent setStateCreator(@NotNull BiFunction<Menu, UpdateState, SendState> creator) {
		Checks.notNull(creator, "creator");
		this.creator = creator;
		return this;
	}
}
