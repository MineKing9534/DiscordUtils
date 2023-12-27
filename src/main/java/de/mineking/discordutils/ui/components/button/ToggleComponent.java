package de.mineking.discordutils.ui.components.button;

import de.mineking.discordutils.ui.components.button.label.EmojiLabel;
import de.mineking.discordutils.ui.components.button.label.LabelProvider;
import de.mineking.discordutils.ui.components.button.label.TextLabel;
import de.mineking.discordutils.ui.state.MessageSendState;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ToggleComponent extends ButtonComponent {
	@Override
	public void register(@NotNull MessageSendState state, @Nullable IReplyCallback event) {
		state.setState(name, current -> current == null ? false : current);
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color A function to get the {@link ButtonColor} for the current state
	 * @param label The label to use
	 */
	public ToggleComponent(@NotNull String name, @NotNull Function<Boolean, ButtonColor> color, @NotNull LabelProvider label) {
		super(name, state -> color.apply(state.getState(name)), label);

		appendHandler(state -> {
			state.<Boolean>setState(name, current -> !current);
			state.update();
		});
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color A function to get the {@link ButtonColor} for the current state
	 * @param label The label to use
	 */
	public ToggleComponent(@NotNull String name, @NotNull Function<Boolean, ButtonColor> color, @NotNull String label) {
		this(name, color, (TextLabel) state -> label);
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color A function to get the {@link ButtonColor} for the current state
	 * @param label The label to use
	 */
	public ToggleComponent(@NotNull String name, @NotNull Function<Boolean, ButtonColor> color, @NotNull Emoji label) {
		this(name, color, (EmojiLabel) state -> label);
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public ToggleComponent(@NotNull String name, @NotNull ButtonColor color, @NotNull LabelProvider label) {
		this(name, state -> color, label);
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public ToggleComponent(@NotNull String name, @NotNull ButtonColor color, @NotNull String label) {
		this(name, state -> color, (TextLabel) state -> label);
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public ToggleComponent(@NotNull String name, @NotNull ButtonColor color, @NotNull Emoji label) {
		this(name, state -> color, (EmojiLabel) state -> label);
	}
}
