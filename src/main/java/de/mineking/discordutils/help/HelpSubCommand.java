package de.mineking.discordutils.help;

import de.mineking.discordutils.commands.Command;
import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

public class HelpSubCommand<C extends ICommandContext> extends Command<C> {
	private final HelpManager<C> manager;
	private final Command<C> command;

	HelpSubCommand(CommandManager<C, ?> cmdMan, HelpManager<C> manager, Command<C> command) {
		super(cmdMan, "help");
		this.manager = manager;
		this.command = command;
	}

	@Override
	public void performCommand(@NotNull C context) throws Exception {
		manager.display(command.getName(), context.getEvent());
	}

	/**
	 * @param name The name for this command
	 * @return {@code this}
	 */
	@NotNull
	public HelpSubCommand<C> withName(@NotNull String name) {
		Checks.notNull(name, "name");

		this.name = name;
		return this;
	}

	/**
	 * @param description The description for this command
	 * @return {@code this}
	 */
	@NotNull
	public HelpSubCommand<C> withDescription(@NotNull String description) {
		Checks.notNull(description, "description");

		this.description = description;
		return this;
	}
}
