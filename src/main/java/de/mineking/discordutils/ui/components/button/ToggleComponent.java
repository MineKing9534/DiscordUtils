package de.mineking.discordutils.ui.components.button;

import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.SendState;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ToggleComponent extends ButtonComponent {
	@Override
	public void register(@NotNull SendState state) {
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
		this(name, color, new LabelProvider() {
			@Override
			public String getText(@NotNull DataState state) {
				return label;
			}
		});
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color A function to get the {@link ButtonColor} for the current state
	 * @param label The label to use
	 */
	public ToggleComponent(@NotNull String name, @NotNull Function<Boolean, ButtonColor> color, @NotNull Emoji label) {
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
	public ToggleComponent(@NotNull String name, @NotNull ButtonColor color, @NotNull LabelProvider label) {
		this(name, state -> color, label);
	}

	/**
	 * @param name  The name of this component. Has to be unique on a menu
	 * @param color The {@link ButtonColor}
	 * @param label The label to use
	 */
	public ToggleComponent(@NotNull String name, @NotNull ButtonColor color, @NotNull String label) {
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
	public ToggleComponent(@NotNull String name, @NotNull ButtonColor color, @NotNull Emoji label) {
		this(name, state -> color, new LabelProvider() {
			@Override
			public Emoji getEmoji(@NotNull DataState state) {
				return label;
			}
		});
	}
}