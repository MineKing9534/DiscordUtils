package de.mineking.discord.ui.components.select;

import de.mineking.discord.ui.MenuBase;
import de.mineking.discord.ui.components.BaseComponent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StringSelectComponent extends BaseComponent<StringSelectInteractionEvent> {
	public StringSelectComponent(String id, Consumer<StringSelectMenu.Builder> config) {
		super(StringSelectInteractionEvent.class, i -> {
			var builder = StringSelectMenu.create(i + ":" + id);
			config.accept(builder);
			return builder.build();
		});
	}

	public StringSelectComponent handle(BiConsumer<MenuBase, StringSelectInteractionEvent> handler) {
		this.handler = handler;
		return this;
	}

	public StringSelectComponent handle(Consumer<StringSelectInteractionEvent> handler) {
		this.handler = (menu, event) -> handler.accept(event);
		return this;
	}
}
