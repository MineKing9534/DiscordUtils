package de.mineking.discord.ui.components;

import de.mineking.discord.ui.MenuBase;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.Arrays;
import java.util.List;

public interface ComponentRow {
	List<? extends Component<?>> getComponents();

	default ActionRow build(MenuBase menu) {
		return ActionRow.of(
				getComponents().stream()
						.map(c -> c.buildComponent(menu))
						.toList()
		);
	}

	static ComponentRow of(Component<?>... components) {
		return of(Arrays.asList(components));
	}

	static ComponentRow of(List<? extends Component<?>> components) {
		return () -> components;
	}
}
