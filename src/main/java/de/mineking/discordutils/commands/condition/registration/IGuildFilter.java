package de.mineking.discordutils.commands.condition.registration;

import de.mineking.discordutils.commands.context.ICommandContext;
import org.jetbrains.annotations.NotNull;

public interface IGuildFilter<C extends ICommandContext> extends IRegistrationCondition<C> {
	@NotNull
	@Override
	default Scope getScope() {
		return Scope.GUILD;
	}
}
