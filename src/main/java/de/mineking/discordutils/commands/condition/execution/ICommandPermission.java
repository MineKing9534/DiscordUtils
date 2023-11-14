package de.mineking.discordutils.commands.condition.execution;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.ContextBase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ICommandPermission<C extends ContextBase<GenericCommandInteractionEvent>> extends IExecutionCondition<C> {
	boolean isPermitted(CommandManager<C, ?> manager, Member member);

	void handleUnpermitted(CommandManager<C, ?> manager, C context);

	@Override
	default boolean isAllowed(@NotNull CommandManager<C, ?> manager, @NotNull C context) {
		if(!isPermitted(manager, context.event.getMember())) {
			handleUnpermitted(manager, context);
			return false;
		}

		return true;
	}
}
