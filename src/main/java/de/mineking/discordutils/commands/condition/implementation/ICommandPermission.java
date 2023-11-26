package de.mineking.discordutils.commands.condition.implementation;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.condition.IExecutionCondition;
import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICommandPermission<C extends ICommandContext> extends IExecutionCondition<C> {
	@Override
	default boolean isAllowed(@NotNull CommandManager<C, ?> manager, @NotNull C context) {
		if(!isPermitted(manager, context, context.getEvent().getMember())) {
			handleUnpermitted(manager, context);
			return false;
		}

		return true;
	}

	/**
	 * @param manager The {@link CommandManager}
	 * @param context The {@link C}
	 * @param member  The {@link Member} who tried to execute the command
	 * @return Whether this execution is permitted
	 */
	default boolean isPermitted(@NotNull CommandManager<C, ?> manager, @NotNull C context, @Nullable Member member) {
		return true;
	}

	/**
	 * @param manager The responsible {@link CommandManager}
	 * @param context The {@link C}
	 */
	default void handleUnpermitted(@NotNull CommandManager<C, ?> manager, @NotNull C context) {
	}

	/**
	 * @return The {@link DefaultMemberPermissions} for the command
	 */
	@NotNull
	default DefaultMemberPermissions requiredPermissions() {
		return DefaultMemberPermissions.ENABLED;
	}
}
