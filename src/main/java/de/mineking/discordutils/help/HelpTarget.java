package de.mineking.discordutils.help;

import de.mineking.discordutils.commands.context.IAutocompleteContext;
import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.components.types.ComponentRow;
import de.mineking.discordutils.ui.state.DataState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HelpTarget {
	/**
	 * @return The identifier of this {@link HelpTarget}
	 */
	@NotNull
	String getKey();

	/**
	 * @param state The current {@link DataState}
	 * @return The {@link MessageEmbed} to display
	 */
	@NotNull
	MessageEmbed build(@NotNull DataState<MessageMenu> state);

	/**
	 * @return The components for this {@link HelpTarget}
	 */
	@NotNull
	List<ComponentRow> getComponents();

	/**
	 * @param locale The {@link DiscordLocale}
	 * @return The name of this {@link HelpTarget} to display
	 */
	@NotNull
	default String getDisplay(@NotNull DiscordLocale locale) {
		return getKey();
	}

	/**
	 * @param current The current value
	 * @return Whether this {@link HelpTarget} should be displayed
	 */
	default boolean matches(@NotNull String current) {
		return getKey().toLowerCase().startsWith(current.toLowerCase());
	}

	/**
	 * @param context The current {@link IAutocompleteContext}
	 * @return Whether this {@link HelpTarget} should be displayed
	 */
	default boolean isAvailable(@NotNull IAutocompleteContext context) {
		return true;
	}
}
