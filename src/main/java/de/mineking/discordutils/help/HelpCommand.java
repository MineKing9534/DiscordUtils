package de.mineking.discordutils.help;

import de.mineking.discordutils.commands.Command;
import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

public class HelpCommand<C extends ICommandContext> extends Command<C> {
	private final HelpManager<C> manager;

	HelpCommand(CommandManager<C, ?> cmdMan, HelpManager<C> manager) {
		super(cmdMan, "help");
		this.manager = manager;

		addOption(manager.getTargetOption());
	}

	@Override
	public void performCommand(@NotNull C context) throws Exception {
		manager.display(context.getEvent().getOption(manager.getTargetOption().getName(), OptionMapping::getAsString), context.getEvent());
	}

	/**
	 * @param name The name for this command
	 * @return {@code this}
	 */
	@NotNull
	public HelpCommand<C> withName(@NotNull String name) {
		Checks.notNull(name, "name");

		this.name = name;
		return this;
	}

	/**
	 * @param description The description for this command
	 * @return {@code this}
	 */
	@NotNull
	public HelpCommand<C> withDescription(@NotNull String description) {
		Checks.notNull(description, "description");

		this.description = description;
		return this;
	}
}
