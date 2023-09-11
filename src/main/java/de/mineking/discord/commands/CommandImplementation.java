package de.mineking.discord.commands;

import de.mineking.discord.localization.LocalizationManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class CommandImplementation {
	public final CommandImplementation parent;
	public final Set<CommandImplementation> children;

	public final CommandInfo info;
	public final Function<Object, Object> instance;
	public final Class<?> type;

	public CommandImplementation(CommandImplementation parent, Set<CommandImplementation> children, CommandInfo info, Class<?> type, Function<Object, Object> instance) {
		this.parent = parent;
		this.children = children;
		this.info = info;
		this.instance = instance;
		this.type = type;

		if(instance == null) {
			throw new IllegalArgumentException("null instance for command '" + getPath() + "' provided");
		}
	}

	public ICommandPermission getEffectivePermission() {
		var permission = getPermission();

		return permission == null && parent != null ? parent.getEffectivePermission() : permission;
	}

	public abstract ICommandPermission getPermission();

	public boolean addToGuild(Guild guild) {
		return true;
	}

	public void handle(GenericCommandInteractionEvent event) {
	}

	public void handleAutocomplete(CommandAutoCompleteInteractionEvent event) {
	}

	public List<OptionData> getOptions(LocalizationManager localization) {
		return Collections.emptyList();
	}

	public CommandImplementation getRoot() {
		return parent != null ? parent.getRoot() : this;
	}

	public String getPath(String separator) {
		return (parent == null ? "" : parent.getPath(separator) + separator) + info.name;
	}

	public String getPath() {
		return getPath(" ");
	}

	public CommandData build(CommandManager<?> manager) {
		try {
			var description = manager.getManager().getLocalization().getCommandDescription(this);

			if(info.type != Command.Type.SLASH) {
				return Commands.context(info.type, description.defaultValue)
						.setNameLocalizations(description.values)
						.setGuildOnly(info.guildOnly);
			} else {
				var subcommands = children.stream()
						.filter(c -> c.children.isEmpty())
						.filter(c -> c.info.type == Command.Type.SLASH)
						.map(sub -> {
							var subDescription = manager.getManager().getLocalization().getCommandDescription(sub);

							return new SubcommandData(sub.info.name, subDescription.defaultValue)
									.setDescriptionLocalizations(subDescription.values)
									.addOptions(sub.getOptions(manager.getManager().getLocalization()));
						})
						.toList();

				var groups = children.stream()
						.filter(c -> !c.children.isEmpty())
						.filter(c -> c.info.type == Command.Type.SLASH)
						.map(group -> {
							var groupDescription = manager.getManager().getLocalization().getCommandDescription(group);

							return new SubcommandGroupData(group.info.name, groupDescription.defaultValue)
									.setDescriptionLocalizations(groupDescription.values)
									.addSubcommands(
											group.children.stream()
													.filter(c -> c.children.isEmpty())
													.filter(c -> c.info.type == Command.Type.SLASH)
													.map(sub -> {
														var subDescription = manager.getManager().getLocalization().getCommandDescription(sub);

														return new SubcommandData(sub.info.name, subDescription.defaultValue)
																.setDescriptionLocalizations(subDescription.values)
																.addOptions(sub.getOptions(manager.getManager().getLocalization()));
													})
													.toList()
									);
						})
						.toList();

				var permission = getEffectivePermission();
				var options = getOptions(manager.getManager().getLocalization());

				return Commands.slash(info.name, description.defaultValue)
						.setDescriptionLocalizations(description.values)
						.setGuildOnly(info.guildOnly)
						.setDefaultPermissions(permission != null ? permission.requirePermissions() : DefaultMemberPermissions.ENABLED)
						.addSubcommands(subcommands)
						.addSubcommandGroups(groups)
						.addOptions(options);
			}
		} catch(Exception e) {
			throw new IllegalStateException("Building command '" + getPath() + "' failed", e);
		}
	}
}
