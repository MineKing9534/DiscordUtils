package de.mineking.discordutils.commands.condition.execution;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.ICommandContext;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public interface IExecutionCondition<C extends ICommandContext> {
	/**
	 * @param manager The responsible {@link CommandManager}
	 * @param context The {@link C} holding information about this execution
	 * @return Whether the command may be executed. If this returns false, you should also acknowledge the interaction to avoid "This interaction has failed"
	 */
	boolean isAllowed(@NotNull CommandManager<C, ?> manager, @NotNull C context);

	/**
	 * @param locale The {@link DiscordLocale} for localization
	 * @return A formatted and localized representation of this {@link IExecutionCondition}
	 */
	@NotNull
	default String format(@NotNull DiscordLocale locale) {
		return toString();
	}

	/**
	 * @return A {@link IExecutionCondition} that only permits execution if both this and the other condition return {@code true} in {@link IExecutionCondition#isAllowed(CommandManager, ICommandContext)}
	 */
	@NotNull
	default IExecutionCondition<C> and(@NotNull IExecutionCondition<C> other) {
		return new CombineExecutionCondition<>(this, other, (a, b) -> a && b, " & ");
	}

	/**
	 * @return A {@link IExecutionCondition} that permits execution if this or the other condition return {@code true} in {@link IExecutionCondition#isAllowed(CommandManager, ICommandContext)}
	 */
	@NotNull
	default IExecutionCondition<C> or(@NotNull IExecutionCondition<C> other) {
		return new CombineExecutionCondition<>(this, other, (a, b) -> a || b, " / ");
	}

	/**
	 * @return A {@link IExecutionCondition} that always permits execution
	 */
	@NotNull
	static <C extends ICommandContext> IExecutionCondition<C> always() {
		return (m, c) -> true;
	}

	/**
	 * @return A {@link IExecutionCondition} that never permits execution
	 */
	@NotNull
	static <C extends ICommandContext> IExecutionCondition<C> never() {
		return (m, c) -> false;
	}

	/**
	 * @return A {@link IExecutionCondition} that permits an execution if any of the provided conditions permits it
	 */
	@NotNull
	@SafeVarargs
	static <C extends ICommandContext> IExecutionCondition<C> any(@NotNull IExecutionCondition<C> condition, @NotNull IExecutionCondition<C>... conditions) {
		Checks.notNull(condition, "condition");
		Checks.notNull(conditions, "conditions");

		for(var c : conditions) condition = condition.or(c);
		return condition;
	}

	/**
	 * @return A {@link IExecutionCondition} that only allows an execution if all the provided conditions permit it
	 */
	@NotNull
	@SafeVarargs
	static <C extends ICommandContext> IExecutionCondition<C> all(@NotNull IExecutionCondition<C> condition, @NotNull IExecutionCondition<C>... conditions) {
		Checks.notNull(condition, "condition");
		Checks.notNull(conditions, "conditions");

		for(var c : conditions) condition = condition.and(c);
		return condition;
	}

	class CombineExecutionCondition<C extends ICommandContext> implements IExecutionCondition<C> {
		private final IExecutionCondition<C> a;
		private final IExecutionCondition<C> b;

		private final BiPredicate<Boolean, Boolean> condition;
		private final String delimiter;

		public CombineExecutionCondition(@NotNull IExecutionCondition<C> a, @NotNull IExecutionCondition<C> b, @NotNull BiPredicate<Boolean, Boolean> condition, @NotNull String delimiter) {
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
		public boolean isAllowed(@NotNull CommandManager<C, ?> manager, @NotNull C context) {
			return condition.test(a.isAllowed(manager, context), b.isAllowed(manager, context));
		}

		@NotNull
		@Override
		public String format(@NotNull DiscordLocale locale) {
			return (a instanceof IExecutionCondition.CombineExecutionCondition<?> ? "(" + a.format(locale) + ")" : a.format(locale)) +
					delimiter +
					(b instanceof IExecutionCondition.CombineExecutionCondition<?> ? "(" + b.format(locale) + ")" : b.format(locale));
		}
	}
}
