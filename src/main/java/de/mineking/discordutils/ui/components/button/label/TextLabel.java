package de.mineking.discordutils.ui.components.button.label;

import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.state.DataState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TextLabel extends LabelProvider {
	@Nullable
	@Override
	String getText(@NotNull DataState<MessageMenu> state);
}
