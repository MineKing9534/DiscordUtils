package de.mineking.discord.ui.components;

import de.mineking.discord.ui.MenuBase;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class HandleComponent<T extends GenericComponentInteractionCreateEvent> extends BaseComponent<T> {
	private BiConsumer<MenuBase, T> handler;

	public HandleComponent(@NotNull Class<T> type, String id) {
		super(type, id);
	}

	public HandleComponent<T> addHandler(@NotNull BiConsumer<MenuBase, T> handler) {
		final var old = this.handler;
		this.handler = (menu, event) -> {
			if(old != null) old.accept(menu, event);
			handler.accept(menu, event);
		};
		return this;
	}

	public HandleComponent<T> addHandler(@NotNull Consumer<MenuBase> handler) {
		return addHandler((menu, event) -> handler.accept(menu));
	}

	public HandleComponent<T> prependHandler(@NotNull BiConsumer<MenuBase, T> handler) {
		final var old = this.handler;
		this.handler = (menu, event) -> {
			handler.accept(menu, event);
			if(old != null) old.accept(menu, event);
		};
		return this;
	}

	public HandleComponent<T> prependHandler(@NotNull Consumer<MenuBase> handler) {
		return prependHandler((menu, event) -> handler.accept(menu));
	}

	public HandleComponent<T> defer() {
		prependHandler((m, event) -> event.deferEdit().queue());
		return this;
	}

	@Override
	public HandleComponent<T> asDisabled(boolean state) {
		super.asDisabled(state);
		return this;
	}

	@Override
	public HandleComponent<T> asDisabled(BooleanSupplier state) {
		super.asDisabled(state);
		return this;
	}

	@Override
	public final void handleParsed(MenuBase menu, T event) {
		if(handler != null) handler.accept(menu, event);
	}
}
