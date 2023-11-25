package de.mineking.discordutils.ui.components.types;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

	/**
	 * @param components The components to use
	 * @return A list of {@link ComponentRow}s holding teh provided components
	 */
	@NotNull
	static List<ComponentRow> ofMany(@NotNull List<Component<?>> components) {
		var result = new ArrayList<ComponentRow>();
		var temp = new ArrayList<Component<?>>();

		for(var c : components) {
			if(5 - temp.size() < c.requiredSpace()) {
				result.add(ComponentRow.of(temp));
				temp.clear();
			}

			temp.add(c);
		}

		if(!temp.isEmpty()) result.add(ComponentRow.of(temp));

		return result;
	}

	/**
	 * @param components The components to use
	 * @return A list of {@link ComponentRow}s holding teh provided components
	 */
	@NotNull
	static List<ComponentRow> ofMany(@NotNull Component<?>... components) {
		return ofMany(Arrays.asList(components));
	}
}
