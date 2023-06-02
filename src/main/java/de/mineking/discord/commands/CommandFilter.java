package de.mineking.discord.commands;

import net.dv8tion.jda.api.interactions.commands.Command;

public interface CommandFilter {
	CommandFilter ALL = (manager, command) -> true;
	CommandFilter NONE = ALL.invert();
	CommandFilter GLOBAL = (manager, command) -> command.getRoot().info.feature.isEmpty();
	CommandFilter FEATURE = GLOBAL.invert();
	CommandFilter TOP = (manager, command) -> command.parent == null || command.info.type != Command.Type.SLASH;
	CommandFilter HAS_SUBCOMMANDS = (manager, command) -> !command.children.isEmpty();

	static CommandFilter path(String path) {
		return (manager, command) -> command.getPath().equals(path);
	}

	static CommandFilter name(String name) {
		return (manager, command) -> {
			var temp = command.getPath().split(" ");

			return name.equals(temp[temp.length - 1]);
		};
	}

	static CommandFilter type(Command.Type type) {
		return (manager, command) -> command.info.type == type;
	}

	static CommandFilter subcommandOf(CommandImplementation parent) {
		return (manager, command) -> command.parent == parent;
	}

	static CommandFilter feature(String feature) {
		return (manager, command) -> command.info.feature.equals(feature);
	}

	boolean test(CommandManager<?> manager, CommandImplementation command);

	default CommandFilter invert() {
		return (manager, command) -> !test(manager, command);
	}

	default CommandFilter or(CommandFilter other) {
		return (manager, command) -> test(manager, command) || other.test(manager, command);
	}

	default CommandFilter and(CommandFilter other) {
		return (manager, command) -> test(manager, command) && other.test(manager, command);
	}

	static CommandFilter any(CommandFilter filter, CommandFilter... filters) {
		for(var f : filters) {
			filter = filter.or(f);
		}

		return filter;
	}

	static CommandFilter all(CommandFilter filter, CommandFilter... filters) {
		for(var f : filters) {
			filter = filter.and(f);
		}

		return filter;
	}
}
