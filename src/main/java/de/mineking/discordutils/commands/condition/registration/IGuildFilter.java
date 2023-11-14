package de.mineking.discordutils.commands.condition.registration;

import de.mineking.discordutils.commands.context.ContextBase;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IGuildFilter<C extends ContextBase<GenericCommandInteractionEvent>> extends IRegistrationCondition<C> {
	@NotNull
	@Override
	default Scope getScope() {
		return Scope.GUILD;
	}
}
