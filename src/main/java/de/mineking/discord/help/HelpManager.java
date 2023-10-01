package de.mineking.discord.help;

import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import de.mineking.discord.commands.CommandFilter;
import de.mineking.discord.commands.CommandImplementation;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class HelpManager extends Module {
	private final Set<HelpTarget> targets = new HashSet<>();

	private final HelpTarget mainTarget;

	public HelpManager(DiscordUtils manager, boolean helpSubcommands, HelpTarget mainTarget, CommandFilter commandFilter, Function<CommandImplementation, HelpTarget> commandTarget) {
		super(manager);

		this.mainTarget = mainTarget;

		var commandManager = manager.getCommandManager();
		var targets = new HashMap<CommandImplementation, HelpTarget>();

		commandManager.findCommands(commandFilter).forEach(impl -> targets.put(impl, commandTarget.apply(impl)));

		if(helpSubcommands) {
			commandManager.findCommands(CommandFilter.TOP.and(CommandFilter.HAS_SUBCOMMANDS))
					.forEach(cmd -> commandManager.registerCommand(cmd, new HelpSubcommand(targets.get(cmd))));
		}

		targets.values().forEach(this::registerTarget);

		commandManager.registerCommand(HelpCommand.class);
	}

	public HelpTarget getMainTarget() {
		return mainTarget;
	}

	public void displayHelp(IReplyCallback event, HelpTarget target) {
		event.reply(target.build(event)).setEphemeral(true).queue();
	}

	public HelpManager registerTarget(HelpTarget target) {
		targets.add(target);
		return this;
	}

	public Set<HelpTarget> getTargets() {
		return Collections.unmodifiableSet(targets);
	}

	public Stream<HelpTarget> findTargets(String current, GenericInteractionCreateEvent event) {
		return targets.stream()
				.filter(t -> t.matches(current))
				.filter(t -> t.isAvailable(event));
	}

	public Optional<HelpTarget> getTarget(String key) {
		return targets.stream()
				.filter(t -> t.getKey().equals(key))
				.findFirst();
	}
}
