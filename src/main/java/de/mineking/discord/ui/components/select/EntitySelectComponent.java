package de.mineking.discord.ui.components.select;

import de.mineking.discord.ui.MenuBase;
import de.mineking.discord.ui.components.HandleComponent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;

import java.util.Arrays;
import java.util.function.Consumer;

public class EntitySelectComponent extends HandleComponent<EntitySelectInteractionEvent> {
	private final Consumer<EntitySelectMenu.Builder> config;
	private final EntitySelectMenu.SelectTarget[] targets;

	public EntitySelectComponent(String id, Consumer<EntitySelectMenu.Builder> config, EntitySelectMenu.SelectTarget... targets) {
		super(EntitySelectInteractionEvent.class, id);
		this.config = config;
		this.targets = targets;
	}

	@Override
	public EntitySelectMenu getComponent(String id, MenuBase menu) {
		var builder = EntitySelectMenu.create(id, Arrays.asList(targets));
		config.accept(builder);
		return builder.build();
	}
}
