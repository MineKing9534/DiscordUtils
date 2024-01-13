package de.mineking.discordutils.ui;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.Manager;
import de.mineking.discordutils.events.EventManager;
import de.mineking.discordutils.ui.components.types.ComponentRow;
import de.mineking.discordutils.ui.modal.ModalMenu;
import de.mineking.discordutils.ui.modal.ModalResponse;
import de.mineking.discordutils.ui.modal.TextComponent;
import de.mineking.discordutils.ui.state.DataState;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class UIManager extends Manager {
	private final EventManager eventManager;

	private final Map<String, MessageMenu> menus = new HashMap<>();
	private final Map<String, ModalMenu> modals = new HashMap<>();

	public UIManager(@NotNull DiscordUtils.Builder<?> manager) {
		eventManager = manager.getEventManager();
	}

	/**
	 * @param name The id of the menu
	 * @return The {@link MessageMenu} with the provided id
	 */
	@NotNull
	public MessageMenu getMenu(@NotNull String name) {
		Checks.notNull(name, "name");
		return Optional.ofNullable(menus.get(name)).orElseThrow();
	}

	/**
	 * @param name The id of the modal
	 * @return The {@link ModalMenu} with the provided id
	 */
	@NotNull
	public ModalMenu getModal(@NotNull String name) {
		Checks.notNull(name, "name");
		return Optional.ofNullable(modals.get(name)).orElseThrow();
	}

	/**
	 * Creates a new menu with the provided id. If a menu with that identifier already exists, the current menu is returned.
	 *
	 * @param identifier The identifier of this menu, used to make menus persistent over restarts
	 * @param renderer   A {@link MessageRenderer} to build the message
	 * @param components The {@link ComponentRow}s
	 * @return The resulting {@link MessageMenu}
	 */
	@NotNull
	public MessageMenu createMenu(@NotNull String identifier, @NotNull MessageRenderer renderer, @NotNull List<ComponentRow> components) {
		Checks.notNull(identifier, "identifier");
		Checks.notNull(renderer, "embed");
		Checks.notNull(components, "components");

		if(identifier.contains(":")) throw new IllegalArgumentException("Id may not contain ':'");
		if(menus.containsKey(identifier)) return menus.get(identifier);

		var menu = new MessageMenu(this, identifier, renderer, components);

		components.stream().flatMap(c -> c.getComponents().stream()).forEach(c -> eventManager.addEventHandler(c.createHandler(menu, event -> event.getComponentId().startsWith(identifier + ":" + c.name + ":"))));

		menus.put(identifier, menu);

		return menu;
	}

	/**
	 * @param identifier The identifier of this menu, used to make menus persistent over restarts
	 * @param renderer   A {@link MessageRenderer} to build the message
	 * @param components The {@link ComponentRow}s
	 * @return The resulting {@link MessageMenu}
	 */
	@NotNull
	public MessageMenu createMenu(@NotNull String identifier, @NotNull MessageRenderer renderer, @NotNull ComponentRow... components) {
		return createMenu(identifier, renderer, Arrays.asList(components));
	}

	/**
	 * @param identifier The identifier of this modal menu
	 * @param title      A function to provide the title
	 * @param components The {@link TextComponent}s
	 * @param handler    A handler function
	 * @return The resulting {@link MessageMenu}
	 */
	@NotNull
	public ModalMenu createModal(@NotNull String identifier, @NotNull Function<DataState<ModalMenu>, String> title, @NotNull List<TextComponent> components, @NotNull BiConsumer<DataState<ModalMenu>, ModalResponse> handler) {
		Checks.notNull(identifier, "identifier");
		Checks.notNull(title, "title");
		Checks.notNull(components, "components");
		Checks.notNull(handler, "handler");

		if(identifier.contains(":")) throw new IllegalArgumentException("Id may not contain ':'");
		if(modals.containsKey(identifier)) return modals.get(identifier);

		var result = new ModalMenu(this, identifier, title, components, handler);

		eventManager.addEventHandler(result.createHandler());
		modals.put(identifier, result);

		return result;
	}
}
