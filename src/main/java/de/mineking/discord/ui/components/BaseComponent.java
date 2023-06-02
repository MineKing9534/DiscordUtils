package de.mineking.discord.ui.components;

import de.mineking.discord.ui.MenuBase;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public abstract class BaseComponent<T extends GenericComponentInteractionCreateEvent> extends Component<T> {
	public Function<String, ActionComponent> component;
	public BiConsumer<MenuBase, T> handler;

	public BooleanSupplier disabled = () -> false;

	public BaseComponent(@NotNull Class<T> type, Function<String, ActionComponent> component) {
		super(type);
		this.component = component;
	}

	public BaseComponent<T> asDisabled(BooleanSupplier state) {
		this.disabled = state;
		return this;
	}

	public BaseComponent<T> asDisabled(boolean state) {
		return asDisabled(() -> state);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void handle(MenuBase menu, GenericComponentInteractionCreateEvent event) {
		if(handler != null) {
			handler.accept(menu, (T) event);
		}
	}

	@Override
	public ActionComponent buildComponent(MenuBase menu) {
		return component.apply(menu.getId()).withDisabled(disabled.getAsBoolean());
	}
}
