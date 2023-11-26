package de.mineking.discordutils.commands.condition.cooldown;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.condition.IExecutionCondition;
import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CooldownImpl<C extends ICommandContext> implements IExecutionCondition<C> {
	private final Map<Long, Instant> cooldown = new HashMap<>();
	private final Duration duration;

	private final BiConsumer<CommandManager<C, ?>, C> handler;

	/**
	 * @param duration The cooldown duration
	 * @param handler  A handler that is executed if an execution is blocked due to the user being on cooldown. You should send an error message here.
	 */
	public CooldownImpl(Duration duration, BiConsumer<CommandManager<C, ?>, C> handler) {
		this.duration = duration;
		this.handler = handler;
	}

	@Override
	public boolean isAllowed(@NotNull CommandManager<C, ?> manager, @NotNull C context) {
		long user = context.getEvent().getUser().getIdLong();

		if(!cooldown.containsKey(user)) return true;
		if(cooldown.get(user).isAfter(Instant.now())) {
			if(handler != null) handler.accept(manager, context);
			return false;
		}

		cooldown.put(user, Instant.now().plus(duration));

		return true;
	}

	@NotNull
	@Override
	public String format(@NotNull DiscordLocale locale) {
		return "Cooldown " + duration.toString();
	}
}
