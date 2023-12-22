package de.mineking.discordutils.ui.components.types;

import de.mineking.discordutils.events.IEventHandler;
import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.state.DataState;
import de.mineking.discordutils.ui.state.MessageSendState;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public abstract class Component<T extends GenericComponentInteractionCreateEvent> implements ComponentRow {
	public final @NotNull String name;

	public Component(@NotNull String name) {
		Checks.notNull(name, "name");

		this.name = name;
	}

	/**
	 * @return The space in a component row that this component requires
	 */
	public abstract int requiredSpace();

	/**
	 * Called once a new state is created. Can be used to create a state required for this component.
	 *
	 * @param state The state
	 */
	public void register(@NotNull MessageSendState state) {
	}

	/**
	 * @param menu   The {@link MessageMenu}
	 * @param filter A filter that specified whether this component should handle the event
	 * @return The {@link IEventHandler} to register or {@code null}
	 */
	@Nullable
	public abstract IEventHandler<T> createHandler(@NotNull MessageMenu menu, @NotNull Predicate<T> filter);

	/**
	 * @param id    The id that the component should have
	 * @param state The {@link DataState}
	 * @return The {@link ActionComponent} to display on Discord
	 */
	@NotNull
	public abstract ActionComponent build(@NotNull String id, @NotNull DataState<MessageMenu> state);

	@NotNull
	@Override
	public final List<Component<?>> getComponents() {
		return List.of(this);
	}
}
