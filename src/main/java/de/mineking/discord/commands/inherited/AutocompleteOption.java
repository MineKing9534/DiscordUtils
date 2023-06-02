package de.mineking.discord.commands.inherited;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.ContextBase;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public abstract class AutocompleteOption<T extends ContextBase> extends Option {
	public AutocompleteOption(OptionType type, String name, String description) {
		super(type, name, description);

		this.autocomplete = true;
	}

	public AutocompleteOption(OptionType type, String name) {
		this(type, name, "");
	}

	public abstract void handleAutocomplete(T context, CommandAutoCompleteInteractionEvent event);

	@SuppressWarnings("unchecked")
	public final void handle(CommandAutoCompleteInteractionEvent event, CommandManager<?> manager) {
		handleAutocomplete((T) manager.getContext().createContext(manager, event), event);
	}
}
