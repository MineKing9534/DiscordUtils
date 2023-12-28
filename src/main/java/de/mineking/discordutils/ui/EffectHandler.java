package de.mineking.discordutils.ui;

import de.mineking.discordutils.ui.state.DataState;

public interface EffectHandler<T> {
	void handle(DataState<?> state, String name, T oldValue, T newValue);
}
