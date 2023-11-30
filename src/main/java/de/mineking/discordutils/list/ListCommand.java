package de.mineking.discordutils.list;

import de.mineking.discordutils.commands.Command;
import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.condition.IExecutionCondition;
import de.mineking.discordutils.commands.context.ICommandContext;
import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.state.MessageSendState;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ListCommand<C extends ICommandContext> extends Command<C> {
	private final Function<String, MessageMenu> menu;
	private final BiConsumer<C, MessageSendState> state;

	private final String pageName;

	ListCommand(Function<String, MessageMenu> menu, BiConsumer<C, MessageSendState> state, CommandManager<C, ?> manager, OptionData option) {
		super(manager, "list");

		this.menu = menu;
		this.state = state;

		addOption(option);
		this.pageName = option.getName();
	}

	@Override
	public void performCommand(@NotNull C context) throws Exception {
		var state = menu.apply(getPath(".")).createState();

		state.setState("page", context.getEvent().getOption(pageName, 1, OptionMapping::getAsInt));

		this.state.accept(context, state);
		state.display(context.getEvent());
	}

	/**
	 * @param name The name for this command
	 * @return {@code this}
	 */
	@NotNull
	public ListCommand<C> withName(@NotNull String name) {
		Checks.notNull(name, "name");

		this.name = name;
		return this;
	}

	/**
	 * @param description The description for this command
	 * @return {@code this}
	 */
	@NotNull
	public ListCommand<C> withDescription(@NotNull String description) {
		Checks.notNull(description, "description");

		this.description = description;
		return this;
	}

	/**
	 * @param condition The {@link IExecutionCondition} for this command
	 * @return {@code this}
	 */
	@NotNull
	public ListCommand<C> withCondition(@Nullable IExecutionCondition<C> condition) {
		this.condition = condition;
		return this;
	}
}
