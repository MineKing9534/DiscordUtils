package de.mineking.discordutils.ui;

import de.mineking.discordutils.ui.state.State;

public interface EffectHandler<T> {
	void handle(State state, String name, T oldValue, T newValue);
}
