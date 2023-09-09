package de.mineking.discord.ui.components.button;

import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.function.Function;

public class ToggleButton extends ToggleButtonBase {
	public final static Function<Boolean, ButtonColor> grayGreen = state -> state ? ButtonColor.GREEN : ButtonColor.GRAY;
	public final static Function<Boolean, ButtonColor> blueGreen = state -> state ? ButtonColor.GREEN : ButtonColor.BLUE;
	public final static Function<Boolean, ButtonColor> redGreen = state -> state ? ButtonColor.GREEN : ButtonColor.RED;
	public final static Function<Boolean, ButtonColor> grayBlue = state -> state ? ButtonColor.BLUE : ButtonColor.GRAY;

	private final Function<Boolean, ButtonColor> color;
	private final Function<Boolean, ButtonLabel> label;

	public ToggleButton(String id, ToggleHolder holder, Function<Boolean, ButtonColor> color, Function<Boolean, ButtonLabel> label) {
		super(id, holder);
		this.color = color;
		this.label = label;
	}

	public ToggleButton(String id, boolean state, Function<Boolean, ButtonColor> color, Function<Boolean, ButtonLabel> label) {
		this(id, new ToggleHolder.DefaultToggleHolder(state), color, label);
	}

	public ToggleButton(String id, Function<Boolean, ButtonColor> color, Function<Boolean, ButtonLabel> label) {
		this(id, false, color, label);
	}

	public ToggleButton(String id, ToggleHolder holder, ButtonColor color, Function<Boolean, ButtonLabel> label) {
		this(id, holder, state -> color, label);
	}

	public ToggleButton(String id, boolean state, ButtonColor color, Function<Boolean, ButtonLabel> label) {
		this(id, new ToggleHolder.DefaultToggleHolder(state), color, label);
	}

	public ToggleButton(String id, ButtonColor color, Function<Boolean, ButtonLabel> label) {
		this(id, false, color, label);
	}

	public ToggleButton(String id, ToggleHolder holder, Function<Boolean, ButtonColor> color, String label) {
		this(id, holder, color, state -> new ButtonLabel(label));
	}

	public ToggleButton(String id, boolean state, Function<Boolean, ButtonColor> color, String label) {
		this(id, new ToggleHolder.DefaultToggleHolder(state), color, label);
	}

	public ToggleButton(String id, Function<Boolean, ButtonColor> color, String label) {
		this(id, false, color, label);
	}

	public ToggleButton(String id, ToggleHolder holder, Function<Boolean, ButtonColor> color, Emoji label) {
		this(id, holder, color, state -> new ButtonLabel(label));
	}

	public ToggleButton(String id, boolean state, Function<Boolean, ButtonColor> color, Emoji label) {
		this(id, new ToggleHolder.DefaultToggleHolder(state), color, label);
	}

	public ToggleButton(String id, Function<Boolean, ButtonColor> color, Emoji label) {
		this(id, false, color, label);
	}

	@Override
	public ButtonColor getColor(boolean state) {
		return color.apply(state);
	}

	@Override
	public ButtonLabel getLabel(boolean state) {
		return label.apply(state);
	}
}
