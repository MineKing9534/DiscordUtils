package de.mineking.discord.list;

import de.mineking.discord.commands.CommandPermission;
import de.mineking.discord.commands.ContextBase;
import de.mineking.discord.commands.inherited.BaseCommand;
import de.mineking.discord.commands.inherited.Option;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand<C extends ContextBase, E extends ListEntry, T extends Listable<E>> extends BaseCommand<C> {
	public final ListProvider<C, E, T> handler;

	public ListCommand(CommandPermission permission, ListProvider<C, E, T> handler) {
		this(permission, handler, Collections.emptyList());
	}

	public ListCommand(CommandPermission permission, ListProvider<C, E, T> handler, List<Option> options) {
		options.forEach(this::addOption);
		addOption(new Option(OptionType.INTEGER, "page", "page").localizeCustom().range(1, null));

		this.permission = permission;
		this.handler = handler;
	}

	@Override
	public void performCommand(C context, GenericCommandInteractionEvent event) {
		event.deferReply(true).queue();

		var listable = handler.getObject(context, event.getOptions().stream().collect(Collectors.toMap(OptionMapping::getName, OptionMapping::getAsString)));

		if(listable == null) {
			return;
		}

		context.manager.getManager().getListManager().sendList(event, event.getOption("page", 1, OptionMapping::getAsInt), listable);
	}
}
