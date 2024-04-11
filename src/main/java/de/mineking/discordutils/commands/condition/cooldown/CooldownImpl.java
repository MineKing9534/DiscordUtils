package de.mineking.discordutils.commands.condition.cooldown;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.condition.IExecutionCondition;
import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class CooldownImpl<C extends ICommandContext> implements IExecutionCondition<C> {
	public final Cache<Long, AtomicInteger> cooldown;

	public final boolean auto;
	public final Duration duration;
	public final int uses;

	public final BiConsumer<CommandManager<C, ?>, C> handler;

	/**
	 * @param duration The cooldown duration
	 * @param uses     The allowed number of uses in the provided interval
	 * @param handler  A handler that is executed if an execution is blocked due to the user being on cooldown. You should send an error message here.
	 * @param auto     Whether to automatically increment the cooldown
	 */
	public CooldownImpl(@NotNull Duration duration, int uses, boolean auto, @NotNull BiConsumer<CommandManager<C, ?>, C> handler) {
		cooldown = Caffeine.newBuilder().expireAfterWrite(duration).build();

		this.auto = auto;
		this.duration = duration;
		this.uses = uses;
		this.handler = handler;
	}

	@Override
	public boolean isAllowed(@NotNull CommandManager<C, ?> manager, @NotNull C context) {
		long user = context.getEvent().getUser().getIdLong();

		var current = cooldown.getIfPresent(user);

		if(current == null) cooldown.put(user, current = new AtomicInteger());
		else if (auto) increment(user);

		if(current.get() >= uses) handler.accept(manager, context);

		return current.get() < uses;
	}

	public void increment(long user) {
		var cooldown = this.cooldown.getIfPresent(user);
		if (cooldown != null) cooldown.getAndIncrement();
	}

	@NotNull
	@Override
	public String format(@NotNull DiscordLocale locale) {
		return "Cooldown " + duration.toString();
	}
}
