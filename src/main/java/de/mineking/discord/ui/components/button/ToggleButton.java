package de.mineking.discord.ui.components.button;

import de.mineking.discord.ui.components.BaseComponent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.function.Function;

public class ToggleButton extends BaseComponent<ButtonInteractionEvent> {
	public static final Function<Boolean, ButtonColor> grayGreen = state -> state ? ButtonColor.GREEN : ButtonColor.GRAY;
	public static final Function<Boolean, ButtonColor> blueGreen = state -> state ? ButtonColor.GREEN : ButtonColor.BLUE;
	public static final Function<Boolean, ButtonColor> redGreen = state -> state ? ButtonColor.GREEN : ButtonColor.RED;
	public static final Function<Boolean, ButtonColor> grayBlue = state -> state ? ButtonColor.BLUE : ButtonColor.GRAY;

	public static Function<Boolean, ButtonColor> defaultColor = grayGreen;

	public final ToggleHolder state;

	public ToggleButton(String id, ToggleHolder state, Function<Boolean, ButtonColor> color, String label) {
		super(ButtonInteractionEvent.class, null);

		this.state = state;

		component = i -> Button.of(
				color.apply(state.getState()).style,
				i + ":" +  id,
				label
		);

		handler = (menu, event) -> {
			state.setState(!state.getState());
			menu.update();
		};
	}

	public ToggleButton(String id, ToggleHolder state, Function<Boolean, ButtonColor> color, Emoji label) {
		super(ButtonInteractionEvent.class, null);

		this.state = state;

		component = i -> Button.of(
				color.apply(state.getState()).style,
				i + id,
				label
		);

		handler = (menu, event) -> {
			state.setState(!state.getState());
			menu.update();
		};
	}

	public ToggleButton(String id, ToggleHolder state, String label) {
		this(id, state, defaultColor, label);
	}

	public ToggleButton(String id, ToggleHolder state, Emoji label) {
		this(id, state, defaultColor, label);
	}


	public ToggleButton(String id, boolean state, Function<Boolean, ButtonColor> color, String label) {
		this(id, new ToggleHolder.DefaultToggleHolder(state), color, label);
	}

	public ToggleButton(String id, boolean state, Function<Boolean, ButtonColor> color, Emoji label) {
		this(id, new ToggleHolder.DefaultToggleHolder(state), color, label);
	}

	public ToggleButton(String id, boolean state, String label) {
		this(id, state, defaultColor, label);
	}

	public ToggleButton(String id, boolean state, Emoji label) {
		this(id, state, defaultColor, label);
	}
}
