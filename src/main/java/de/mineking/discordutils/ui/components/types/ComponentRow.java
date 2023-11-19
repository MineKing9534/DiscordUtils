package de.mineking.discordutils.ui.components.types;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public interface ComponentRow extends Iterable<Component<?>> {
	/**
	 * @return The components of this {@link ComponentRow}
	 */
	@NotNull
	List<Component<?>> getComponents();

	@NotNull
	@Override
	default Iterator<Component<?>> iterator() {
		return getComponents().iterator();
	}

	/**
	 * @param components The components to use
	 * @return A {@link ComponentRow} holding the provided components
	 */
	@NotNull
	static ComponentRow of(@NotNull List<Component<?>> components) {
		Checks.notNull(components, "components");

		return () -> components;
	}

	/**
	 * @param components The components to use
	 * @return A {@link ComponentRow} holding the provided components
	 */
	@NotNull
	static ComponentRow of(@NotNull Component<?>... components) {
		return of(Arrays.asList(components));
	}
}
