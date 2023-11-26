package de.mineking.discordutils.ui.components.button.label;

import de.mineking.discordutils.ui.state.DataState;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EmojiLabel extends LabelProvider {
	@Nullable
	@Override
	Emoji getEmoji(@NotNull DataState state);
}
