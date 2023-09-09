package de.mineking.discord.ui.components.select;

import de.mineking.discord.ui.MenuBase;
import de.mineking.discord.ui.components.BaseComponent;
import de.mineking.discord.ui.components.HandleComponent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StringSelectComponent extends HandleComponent<StringSelectInteractionEvent> {
	private final Consumer<StringSelectMenu.Builder> config;

	public StringSelectComponent(String id, Consumer<StringSelectMenu.Builder> config) {
		super(StringSelectInteractionEvent.class, id);
		this.config = config;
	}

	@Override
	public StringSelectMenu getComponent(String id, MenuBase menu) {
		var builder = StringSelectMenu.create(id);
		config.accept(builder);
		return builder.build();
	}
}
