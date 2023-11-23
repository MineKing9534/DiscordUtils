package de.mineking.discordutils.commands.condition.execution;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.condition.registration.IRegistrationCondition;
import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICommandPermission<C extends ICommandContext> extends IExecutionCondition<C>, IRegistrationCondition<C> {
	boolean isPermitted(CommandManager<C, ?> manager, Member member);

	void handleUnpermitted(CommandManager<C, ?> manager, C context);

	@Override
	default boolean isAllowed(@NotNull CommandManager<C, ?> manager, @NotNull C context) {
		if(!isPermitted(manager, context.getEvent().getMember())) {
			handleUnpermitted(manager, context);
			return false;
		}

		return true;
	}

	@NotNull
	@Override
	default String format(@NotNull DiscordLocale locale) {
		return toString();
	}

	@Override
	default boolean shouldRegister(@NotNull CommandManager<C, ?> manager, @Nullable Guild guild) {
		return true;
	}
}
