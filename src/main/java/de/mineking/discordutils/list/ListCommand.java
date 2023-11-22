package de.mineking.discordutils.list;

import de.mineking.discordutils.commands.Command;
import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.condition.execution.IExecutionCondition;
import de.mineking.discordutils.commands.context.ContextBase;
import de.mineking.discordutils.ui.Menu;
import de.mineking.discordutils.ui.state.SendState;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ListCommand<C extends ContextBase<? extends GenericCommandInteractionEvent>> extends Command<C> {
	private final Function<String, Menu> menu;
	private final BiConsumer<C, SendState> state;

	private final String pageName;

	ListCommand(Function<String, Menu> menu, BiConsumer<C, SendState> state, CommandManager<C, ?> manager, OptionData option) {
		super(manager, net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH, "list");

		this.menu = menu;
		this.state = state;

		addOption(option);
		this.pageName = option.getName();
	}

	@Override
	public void performCommand(@NotNull C context) throws Exception {
		var state = menu.apply(getPath(".")).createState();

		state.setState("page", context.event.getOption(pageName, 1, OptionMapping::getAsInt));

		this.state.accept(context, state);
		state.display(context.event);
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
