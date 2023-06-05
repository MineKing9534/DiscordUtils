package de.mineking.discord.list;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Map;
import java.util.stream.Collectors;

public interface ListProvider<T extends Listable<E>, E extends ListEntry> {
	T getObject(User user, Guild guild, Map<String, String> params);

	default T getObject(GenericCommandInteractionEvent event) {
		return getObject(event.getUser(), event.getGuild(), event.getOptions().stream().collect(Collectors.toMap(OptionMapping::getName, OptionMapping::getAsString)));
	}
}
