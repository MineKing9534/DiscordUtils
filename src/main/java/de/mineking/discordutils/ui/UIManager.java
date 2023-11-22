package de.mineking.discordutils.ui;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.Manager;
import de.mineking.discordutils.events.EventManager;
import de.mineking.discordutils.ui.components.types.ComponentRow;
import de.mineking.discordutils.ui.state.DataState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class UIManager extends Manager {
	private final EventManager eventManager;

	private final Map<String, Menu> menus = new HashMap<>();

	public UIManager(@NotNull DiscordUtils.Builder<?> manager) {
		eventManager = manager.getManager(EventManager.class);
	}

	/**
	 * @param name The id of the menu
	 * @return The menu with the provided id
	 */
	@NotNull
	public Menu getMenu(@NotNull String name) {
		Checks.notNull(name, "name");
		return Optional.ofNullable(menus.get(name)).orElseThrow();
	}

	/**
	 * Creates a new menu with the provided id. If a menu with that identifier already exists, the current menu is returned.
	 *
	 * @param identifier The identifier of this menu, used to make menus persistent over restarts
	 * @param embed      The {@link MessageEmbed} to display
	 * @param components The {@link ComponentRow}s
	 * @return The resulting {@link Menu}
	 */
	@NotNull
	public Menu createMenu(@NotNull String identifier, @NotNull Function<DataState, MessageEmbed> embed, @NotNull List<ComponentRow> components) {
		Checks.notNull(identifier, "identifier");
		Checks.notNull(embed, "embed");
		Checks.notNull(components, "components");

		if(identifier.contains(":")) throw new IllegalArgumentException("Id may not contain ':'");
		if(menus.containsKey(identifier)) return menus.get(identifier);

		var menu = new Menu(this, identifier, embed, components);

		components.stream()
				.flatMap(c -> c.getComponents().stream())
				.forEach(c ->
						eventManager.addEventHandler(c.createHandler(
								menu,
								event -> event.getComponentId().startsWith(identifier + ":" + c.name + ":"))
						)
				);

		menus.put(identifier, menu);

		return menu;
	}

	/**
	 * @param identifier The identifier of this menu, used to make menus persistent over restarts
	 * @param embed      The {@link MessageEmbed} to display
	 * @param components The {@link ComponentRow}s
	 * @return The resulting {@link Menu}
	 */
	@NotNull
	public Menu createMenu(@NotNull String identifier, @NotNull Function<DataState, MessageEmbed> embed, @NotNull ComponentRow... components) {
		return createMenu(identifier, embed, Arrays.asList(components));
	}
}
