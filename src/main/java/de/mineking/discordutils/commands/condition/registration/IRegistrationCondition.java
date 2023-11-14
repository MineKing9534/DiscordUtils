package de.mineking.discordutils.commands.condition.registration;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.ContextBase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public interface IRegistrationCondition<C extends ContextBase<? extends GenericCommandInteractionEvent>> {
	/**
	 * @param manager The responsible {@link CommandManager}
	 * @param guild   The {@link Guild} to check or {@code null} if this is a global command
	 * @return Whether the command should be registered
	 */
	boolean shouldRegister(@NotNull CommandManager<C, ?> manager, @Nullable Guild guild);

	/**
	 * @return The {@link Scope} of this command. Default: {@link Scope#GLOBAL}
	 * @see Scope
	 */
	@NotNull
	default Scope getScope() {
		return Scope.GLOBAL;
	}

	/**
	 * @return The {@link DefaultMemberPermissions} of this command
	 * @see DefaultMemberPermissions
	 */
	@NotNull
	default DefaultMemberPermissions getPermission() {
		return DefaultMemberPermissions.ENABLED;
	}

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
	 * @return A {@link IRegistrationCondition} that always registers the command
	 */
	@NotNull
	static <C extends ContextBase<? extends GenericCommandInteractionEvent>> IRegistrationCondition<C> always() {
		return (m, g) -> true;
	}

	/**
	 * @return A {@link IRegistrationCondition} that never registers the command
	 */
	@NotNull
	static <C extends ContextBase<? extends GenericCommandInteractionEvent>> IRegistrationCondition<C> never() {
		return (m, g) -> false;
	}

	/**
	 * @return A {@link IRegistrationCondition} that registers the command if any of the provided conditions registers it
	 */
	@NotNull
	@SafeVarargs
	static <C extends ContextBase<? extends GenericCommandInteractionEvent>> IRegistrationCondition<C> any(@NotNull IRegistrationCondition<C> condition, @NotNull IRegistrationCondition<C>... conditions) {
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
	static <C extends ContextBase<? extends GenericCommandInteractionEvent>> IRegistrationCondition<C> all(@NotNull IRegistrationCondition<C> condition, @NotNull IRegistrationCondition<C>... conditions) {
		Checks.notNull(condition, "condition");
		Checks.notNull(conditions, "conditions");

		for(var c : conditions) condition = condition.and(c);
		return condition;
	}

	/**
	 * @return A {@link IRegistrationCondition} that registers the command in the specified {@link Scope}
	 */
	@NotNull
	static <C extends ContextBase<? extends GenericCommandInteractionEvent>> IRegistrationCondition<C> scope(@NotNull Scope scope) {
		Checks.notNull(scope, "scope");

		return new PropertyRegistrationCondition<>(scope, null);
	}

	/**
	 * @return A {@link IRegistrationCondition} that is only visible for the specified {@link DefaultMemberPermissions}
	 */
	@NotNull
	static <C extends ContextBase<? extends GenericCommandInteractionEvent>> IRegistrationCondition<C> permission(@NotNull DefaultMemberPermissions permission) {
		Checks.notNull(permission, "permission");

		return new PropertyRegistrationCondition<>(null, permission);
	}

	class PropertyRegistrationCondition<C extends ContextBase<? extends GenericCommandInteractionEvent>> implements IRegistrationCondition<C> {
		private final DefaultMemberPermissions permission;
		private final Scope scope;

		public PropertyRegistrationCondition(@Nullable Scope scope, @Nullable DefaultMemberPermissions permission) {
			this.scope = scope;
			this.permission = permission;
		}

		@Override
		public boolean shouldRegister(@NotNull CommandManager<C, ?> manager, @Nullable Guild guild) {
			return true;
		}

		@NotNull
		@Override
		public Scope getScope() {
			return scope != null ? scope : IRegistrationCondition.super.getScope();
		}

		@NotNull
		@Override
		public DefaultMemberPermissions getPermission() {
			return permission != null ? permission : IRegistrationCondition.super.getPermission();
		}
	}

	class CombineRegistrationCondition<C extends ContextBase<? extends GenericCommandInteractionEvent>> implements IRegistrationCondition<C> {
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
		public Scope getScope() {
			return a.getScope();
		}

		@NotNull
		@Override
		public DefaultMemberPermissions getPermission() {
			if(a.getPermission().getPermissionsRaw() == null) return b.getPermission();
			if(b.getPermission().getPermissionsRaw() == null) return a.getPermission();

			return DefaultMemberPermissions.enabledFor(a.getPermission().getPermissionsRaw() + b.getPermission().getPermissionsRaw());
		}

		@NotNull
		@Override
		public String format(@NotNull DiscordLocale locale) {
			return (a instanceof IRegistrationCondition.CombineRegistrationCondition<?> ? "(" + a.format(locale) + ")" : a.format(locale)) +
					delimiter +
					(b instanceof IRegistrationCondition.CombineRegistrationCondition<?> ? "(" + b.format(locale) + ")" : b.format(locale));
		}
	}
}
