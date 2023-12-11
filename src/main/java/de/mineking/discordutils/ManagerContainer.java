package de.mineking.discordutils;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.IAutocompleteContext;
import de.mineking.discordutils.commands.context.ICommandContext;
import de.mineking.discordutils.events.EventManager;
import de.mineking.discordutils.help.HelpManager;
import de.mineking.discordutils.languagecache.LanguageCacheManager;
import de.mineking.discordutils.list.ListManager;
import de.mineking.discordutils.ui.UIManager;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface ManagerContainer {
	/**
	 * @return All previously added {@link Manager}s
	 */
	Collection<Manager> getManagers();

	/**
	 * @param type The {@link Class} of the {@link Manager} you want to get
	 * @return An {@link Optional} holding the {@link Manager} if present
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	default <T extends Manager> Optional<T> getManager(@NotNull Class<T> type) {
		Checks.notNull(type, "type");

		return getManagers().stream()
				.filter(m -> m.getClass().equals(type))
				.map(m -> (T) m)
				.findAny();
	}

	/**
	 * @return The {@link CommandManager} previously registered on this {@link DiscordUtils} instance
	 * @throws IllegalStateException If no {@link CommandManager} is registered
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	default <C extends ICommandContext, A extends IAutocompleteContext> CommandManager<C, A> getCommandManager() throws IllegalStateException {
		return (CommandManager<C, A>) getManager(CommandManager.class).orElseThrow(IllegalStateException::new);
	}

	/**
	 * @return The {@link LanguageCacheManager} previously registered on this {@link DiscordUtils} instance
	 * @throws IllegalStateException If no {@link LanguageCacheManager} is registered
	 */
	@NotNull
	default LanguageCacheManager getLanguageCache() throws IllegalStateException {
		return getManager(LanguageCacheManager.class).orElseThrow(IllegalStateException::new);
	}

	/**
	 * @return The {@link EventManager} previously registered on this {@link DiscordUtils} instance
	 * @throws IllegalStateException If no {@link EventManager} is registered
	 */
	@NotNull
	default EventManager getEventManager() throws IllegalStateException {
		return getManager(EventManager.class).orElseThrow(IllegalStateException::new);
	}

	/**
	 * @return The {@link UIManager} previously registered on this {@link DiscordUtils} instance
	 * @throws IllegalStateException If no {@link UIManager} is registered
	 */
	@NotNull
	default UIManager getUIManager() throws IllegalStateException {
		return getManager(UIManager.class).orElseThrow(IllegalStateException::new);
	}

	/**
	 * @return The {@link ListManager} previously registered on this {@link DiscordUtils} instance
	 * @throws IllegalStateException If no {@link ListManager} is registered
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	default <C extends ICommandContext> ListManager<C> getListManager() {
		return (ListManager<C>) getManager(ListManager.class).orElseThrow(IllegalStateException::new);
	}

	/**
	 * @return The {@link HelpManager} previously registered on this {@link DiscordUtils} instance
	 * @throws IllegalStateException If no {@link HelpManager} is registered
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	default  <C extends ICommandContext> HelpManager<C> getHelpManager() {
		return (HelpManager<C>) getManager(HelpManager.class).orElseThrow(IllegalStateException::new);
	}
}
