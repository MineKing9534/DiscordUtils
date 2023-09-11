package de.mineking.discord.commands.inherited;

import de.mineking.discord.commands.*;
import de.mineking.discord.commands.exception.CommandExecutionException;
import de.mineking.discord.localization.LocalizationManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class BaseCommand<C extends ContextBase> {
	private final Command.Type type;
	private final List<Option> options = new LinkedList<>();
	private final Map<String, BaseCommand<C>> subcommands = new HashMap<>();

	public ICommandPermission permission = null;

	public String description = "";
	public String feature = "";

	public BaseCommand(@NotNull Command.Type type) {
		Checks.notNull(type, "type");
		this.type = type;
	}

	public BaseCommand() {
		this(Command.Type.SLASH);
	}

	public boolean addToGuild(Guild guild) {
		return true;
	}

	public void performCommand(C context, GenericCommandInteractionEvent event) {
	}

	protected BaseCommand<C> addOption(Option option) {
		options.add(option);

		return this;
	}

	protected BaseCommand<C> addSubcommand(String name, BaseCommand<C> command) {
		subcommands.put(name, command);

		return this;
	}

	public Map<String, CommandImplementation> build(CommandManager<?> manager, String name) {
		return build(manager, null, name);
	}

	public Map<String, CommandImplementation> build(CommandManager<?> manager, CommandImplementation parent, String name) {
		var result = new HashMap<String, CommandImplementation>();

		var children = new HashSet<CommandImplementation>();

		var impl = getImplementation(manager, parent, children, name);
		result.put((parent == null ? "" : parent.getPath() + " ") + name, impl);

		subcommands.entrySet().stream()
				.map(e -> e.getValue().build(manager, impl, e.getKey()))
				.forEach(cmd -> {
					result.putAll(cmd);
					children.addAll(cmd.values());
				});

		return result;
	}

	@SuppressWarnings("unchecked")
	private CommandImplementation getImplementation(CommandManager<?> manager, CommandImplementation parent, Set<CommandImplementation> children, String name) {
		return new CommandImplementation(parent, children, new CommandInfo(name, description, feature, type, false, false), getClass(), ctx -> this) {
			@Override
			public boolean addToGuild(Guild guild) {
				return BaseCommand.this.addToGuild(guild);
			}

			@Override
			public void handle(GenericCommandInteractionEvent event) {
				try {
					var permission = getEffectivePermission();

					if(permission != null && !permission.isPermitted(manager, event)) {
						permission.handleUnpermitted(manager, event);
						return;
					}

					performCommand((C) manager.getContext().createContext(manager, event), event);
				} catch(Exception e) {
					throw new CommandExecutionException(this, e);
				}
			}

			@Override
			public void handleAutocomplete(CommandAutoCompleteInteractionEvent event) {
				try {
					var permission = getEffectivePermission();

					if(permission != null && !permission.isPermitted(manager, event)) {
						return;
					}

					for(var option : options) {
						if(option.name.equals(event.getFocusedOption().getName()) && option instanceof AutocompleteOption<?> ao) {
							ao.handle(event, manager);
							return;
						}
					}
				} catch(Exception e) {
					throw new CommandExecutionException(this, e);
				}
			}

			@Override
			public List<OptionData> getOptions(LocalizationManager localization) {
				return options.stream().map(o -> o.build(getPath(), localization)).toList();
			}

			@Override
			public ICommandPermission getPermission() {
				return permission;
			}
		};
	}
}
