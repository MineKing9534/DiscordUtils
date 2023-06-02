package de.mineking.discord.ui.components.select;

import de.mineking.discord.ui.MenuBase;
import de.mineking.discord.ui.components.BaseComponent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EntitySelectComponent extends BaseComponent<EntitySelectInteractionEvent> {
	public EntitySelectComponent(String id, Consumer<EntitySelectMenu.Builder> config, EntitySelectMenu.SelectTarget... targets) {
		super(EntitySelectInteractionEvent.class, i -> {
			var builder = EntitySelectMenu.create(i + ":" + id, Arrays.asList(targets));
			config.accept(builder);
			return builder.build();
		});
	}

	public EntitySelectComponent handle(BiConsumer<MenuBase, EntitySelectInteractionEvent> handler) {
		this.handler = handler;
		return this;
	}

	public EntitySelectComponent handle(Consumer<EntitySelectInteractionEvent> handler) {
		this.handler = (menu, event) -> handler.accept(event);
		return this;
	}
}
