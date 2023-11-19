package de.mineking.discordutils.ui.components.button;

import de.mineking.discordutils.ui.state.DataState;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LabelProvider {
	/**
	 * @param state The current {@link DataState}
	 * @return The string to use as label
	 */
	@Nullable
	default String getText(@NotNull DataState state) {
		return null;
	}

	/**
	 * @param state The current {@link DataState}
	 * @return The {@link Emoji} to use as label
	 */
	@Nullable
	default Emoji getEmoji(@NotNull DataState state) {
		return null;
	}
}
