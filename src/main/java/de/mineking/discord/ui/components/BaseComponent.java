package de.mineking.discord.ui.components;

import de.mineking.discord.ui.MenuBase;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public abstract class BaseComponent<T extends GenericComponentInteractionCreateEvent> extends Component<T> {
	private BooleanSupplier disabled;

	public BaseComponent(@NotNull Class<T> type, String id) {
		super(type, id);
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
	public final void handle(MenuBase menu, GenericComponentInteractionCreateEvent event) {
		handleParsed(menu, (T) event);
	}

	public abstract void handleParsed(MenuBase menu, T event);

	@Override
	public final ActionComponent buildComponent(MenuBase menu) {
		return getComponent(getComponentId(menu), menu).withDisabled(disabled != null && disabled.getAsBoolean());
	}

	public abstract ActionComponent getComponent(String id, MenuBase menu);
}
