package de.mineking.discord.ui.components.button;

import de.mineking.discord.ui.MenuBase;
import de.mineking.discord.ui.components.BaseComponent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public abstract class ToggleButtonBase extends BaseComponent<ButtonInteractionEvent> {
	protected final ToggleHolder state;

	public ToggleButtonBase(String id, ToggleHolder state) {
		super(ButtonInteractionEvent.class, id);
		this.state = state;
	}

	public ToggleButtonBase(String id, boolean value) {
		this(id, new ToggleHolder.DefaultToggleHolder(value));
	}

	public ToggleButtonBase(String id) {
		this(id, false);
	}

	public abstract ButtonColor getColor(boolean state);
	public abstract ButtonLabel getLabel(boolean state);

	@Override
	public final void handleParsed(MenuBase menu, ButtonInteractionEvent event) {
		state.setState(!state.getState(), menu, event);
		menu.update();
	}

	@Override
	public final Button getComponent(String id, MenuBase menu) {
		var state = this.state.getState();
		return getLabel(state).build(getColor(state).style, id);
	}
}
