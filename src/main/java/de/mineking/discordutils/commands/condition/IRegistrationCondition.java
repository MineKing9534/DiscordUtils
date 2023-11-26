package de.mineking.discordutils.commands.condition;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public interface IRegistrationCondition<C extends ICommandContext> {
	/**
	 * @param manager The responsible {@link CommandManager}
	 * @param guild   The {@link Guild} to check or {@code null} if this is a global command
	 * @return Whether the command should be registered
	 */
	boolean shouldRegister(@NotNull CommandManager<C, ?> manager, @Nullable Guild guild);

	/**
	 * @param locale The {@link DiscordLocale} for localization
	 * @return A formatted and localized representation of this {@link IRegistrationCondition}
	 */
	@NotNull
	default String format(@NotNull DiscordLocale locale) {
		return toString();
	}

	/**
	 * @return A {@link IRegistrationCondition} that only registers the command if both this and the other condition return {@code true} in {@link #shouldRegister(CommandManager, Guild)}
	 */
	@NotNull
	default IRegistrationCondition<C> and(@NotNull IRegistrationCondition<C> other) {
		return new CombineRegistrationCondition<>(this, other, (a, b) -> a && b, " & ");
	}

	/**
	 * @return A {@link IRegistrationCondition} that registers the command if this or the other condition return {@code true} in {@link #shouldRegister(CommandManager, Guild)}
	 */
	@NotNull
	default IRegistrationCondition<C> or(@NotNull IRegistrationCondition<C> other) {
		return new CombineRegistrationCondition<>(this, other, (a, b) -> a || b, " / ");
	}

	/**
	 * @return All {@link IRegistrationCondition}s
	 */
	@NotNull
	default List<IRegistrationCondition<C>> all() {
		return List.of(this);
	}

	/**
	 * @return A {@link IRegistrationCondition} that always registers the command
	 */
	@NotNull
	static <C extends ICommandContext> IRegistrationCondition<C> always() {
		return (m, g) -> true;
	}

	/**
	 * @return A {@link IRegistrationCondition} that never registers the command
	 */
	@NotNull
	static <C extends ICommandContext> IRegistrationCondition<C> never() {
		return (m, g) -> false;
	}

	/**
	 * @return A {@link IRegistrationCondition} that registers the command if any of the provided conditions registers it
	 */
	@NotNull
	@SafeVarargs
	static <C extends ICommandContext> IRegistrationCondition<C> any(@NotNull IRegistrationCondition<C> condition, @NotNull IRegistrationCondition<C>... conditions) {
		Checks.notNull(condition, "condition");
		Checks.notNull(conditions, "conditions");

		for(var c : conditions) condition = condition.or(c);
		return condition;
	}

	/**
	 * @return A {@link IRegistrationCondition} that only registers a command if all the provided conditions register it
	 */
	@NotNull
	@SafeVarargs
	static <C extends ICommandContext> IRegistrationCondition<C> all(@NotNull IRegistrationCondition<C> condition, @NotNull IRegistrationCondition<C>... conditions) {
		Checks.notNull(condition, "condition");
		Checks.notNull(conditions, "conditions");

		for(var c : conditions) condition = condition.and(c);
		return condition;
	}

	class CombineRegistrationCondition<C extends ICommandContext> implements IRegistrationCondition<C> {
		private final IRegistrationCondition<C> a;
		private final IRegistrationCondition<C> b;

		private final BiPredicate<Boolean, Boolean> condition;
		private final String delimiter;

		public CombineRegistrationCondition(@NotNull IRegistrationCondition<C> a, @NotNull IRegistrationCondition<C> b, @NotNull BiPredicate<Boolean, Boolean> condition, @NotNull String delimiter) {
			Checks.notNull(a, "a");
			Checks.notNull(b, "b");
			Checks.notNull(condition, "condition");
			Checks.notNull(delimiter, "delimiter");

			this.a = a;
			this.b = b;
			this.condition = condition;
			this.delimiter = delimiter;
		}

		@Override
		public boolean shouldRegister(@NotNull CommandManager<C, ?> manager, @Nullable Guild guild) {
			return condition.test(a.shouldRegister(manager, guild), b.shouldRegister(manager, guild));
		}

		@NotNull
		@Override
		public String format(@NotNull DiscordLocale locale) {
			return (a instanceof IRegistrationCondition.CombineRegistrationCondition<?> ? "(" + a.format(locale) + ")" : a.format(locale)) +
					delimiter +
					(b instanceof IRegistrationCondition.CombineRegistrationCondition<?> ? "(" + b.format(locale) + ")" : b.format(locale));
		}

		@NotNull
		@Override
		public List<IRegistrationCondition<C>> all() {
			return Stream.concat(a.all().stream(), b.all().stream()).toList();
		}
	}
}
