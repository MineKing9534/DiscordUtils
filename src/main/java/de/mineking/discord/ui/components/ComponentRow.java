package de.mineking.discord.ui.components;

import de.mineking.discord.ui.MenuBase;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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

	static List<ComponentRow> build(List<? extends Component<?>> components) {
		var result = new LinkedList<ComponentRow>();
		var temp = new LinkedList<Component<?>>();

		for(var c : components) {
			if(c.type.equals(ButtonInteractionEvent.class)) {
				temp.add(c);

				if(temp.size() >= 5) {
					result.add(of(temp));
					temp.clear();
				}
			}

			else {
				if(!temp.isEmpty()) {
					result.add(of(temp));
					temp.clear();
				}

				result.add(of(c));
			}
		}

		if(!temp.isEmpty()) result.add(of(temp));

		return new ArrayList<>(result);
	}
}
