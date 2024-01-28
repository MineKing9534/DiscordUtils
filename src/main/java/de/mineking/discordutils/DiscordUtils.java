package de.mineking.discordutils;

import de.mineking.discordutils.commands.CommandManager;
import de.mineking.discordutils.commands.context.IAutocompleteContext;
import de.mineking.discordutils.commands.context.ICommandContext;
import de.mineking.discordutils.console.DiscordOutputStream;
import de.mineking.discordutils.console.MirrorPrintStream;
import de.mineking.discordutils.console.RedirectTarget;
import de.mineking.discordutils.events.EventManager;
import de.mineking.discordutils.help.HelpManager;
import de.mineking.discordutils.help.HelpTarget;
import de.mineking.discordutils.languagecache.LanguageCacheManager;
import de.mineking.discordutils.list.ListManager;
import de.mineking.discordutils.localization.Localization;
import de.mineking.discordutils.localization.LocalizationFunction;
import de.mineking.discordutils.localization.LocalizationManager;
import de.mineking.discordutils.restaction.CustomRestActionManager;
import de.mineking.discordutils.ui.UIManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiscordUtils<B> extends ListenerAdapter implements ManagerContainer {
	private final JDA jda;
	private final B bot;

	private final LocalizationManager localization;
	private final Set<Manager> managers;

	/**
	 * @param jda Your {@link JDABuilder} instance
	 * @param bot Your bot instance
	 * @return A builder to create a {@link DiscordUtils} instance
	 */
	public static <B> Builder<B> create(@NotNull JDABuilder jda, @Nullable B bot) {
		Checks.notNull(jda, "jda");
		return new Builder<>(jda, bot);
	}

	DiscordUtils(JDABuilder jda, B bot, LocalizationManager localization, Set<Manager> managers) {
		this.bot = bot;
		this.localization = localization;
		this.managers = managers;

		this.jda = jda.build();
		this.jda.addEventListener(this);
	}

	@Override
	public Set<Manager> getManagers() {
		return managers;
	}

	/**
	 * @return The {@link JDA} instance managed by this {@link DiscordUtils} instance
	 */
	@NotNull
	public JDA getJDA() {
		return jda;
	}

	/**
	 * @return Your bot instance
	 */
	@NotNull
	public B getBot() {
		return bot;
	}

	/**
	 * @return Your bot instance
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public <T> T getBot(Class<T> type) {
		return (T) getBot();
	}

	/**
	 * @param type The java type to instantiate
	 * @param args A function to provide a value for a parameter
	 * @return The resulting instance
	 */
	@SuppressWarnings("unchecked")
	public <T> T createInstance(@NotNull Class<T> type, @NotNull BiFunction<Integer, Parameter, Object> args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		Checks.notNull(type, "type");
		Checks.notNull(args, "args");

		var constructor = (Constructor<T>) type.getDeclaredConstructors()[0];
		return constructor.newInstance(createParameters(constructor.getParameters(), args));
	}

	/**
	 * @param method   The method to invoke
	 * @param instance The instance to invoke the method on
	 * @param args     A function to provide a value for a parameter
	 * @return The method's return value
	 */
	public Object invokeMethod(@NotNull Method method, Object instance, @NotNull BiFunction<Integer, Parameter, Object> args) throws InvocationTargetException, IllegalAccessException {
		Checks.notNull(method, "method");
		Checks.notNull(args, "args");

		return method.invoke(instance, createParameters(method.getParameters(), args));
	}

	@SuppressWarnings("unchecked")
	private Object[] createParameters(Parameter[] params, BiFunction<Integer, Parameter, Object> args) {
		var result = new Object[params.length];

		for(int i = 0; i < params.length; i++) {
			var p = params[i];

			if(bot != null && p.getType().isAssignableFrom(bot.getClass())) result[i] = bot;
			else if(p.getType().isAssignableFrom(DiscordUtils.class)) result[i] = this;
			else if(Manager.class.isAssignableFrom(p.getType()))
				result[i] = getManager((Class<? extends Manager>) p.getType()).orElseThrow();
			else result[i] = args.apply(i, p);
		}

		return result;
	}

	/**
	 * @return The {@link LocalizationManager} or {@code null}
	 */
	@Nullable
	public LocalizationManager getLocalizationManager() {
		return localization;
	}

	/**
	 * @param pathSupplier A localization path provider
	 * @param other        A path override. If localization is disabled, this will be returned
	 * @return The localized description
	 */
	@NotNull
	public Localization getLocalization(@NotNull Function<LocalizationFunction, String> pathSupplier, @Nullable String other) {
		Checks.notNull(pathSupplier, "pathSupplier");

		var path = other != null && !other.isEmpty() ? other : pathSupplier.apply((p, l) -> p);

		if(localization == null) return new Localization(path, Collections.emptyMap());

		return new Localization(localization.function().localize(path, localization.defaultLocale()), localization.locales().stream().collect(Collectors.toMap(l -> l, l -> localization.function().localize(path, l))));
	}

	public static class Builder<B> implements ManagerContainer {
		private final Set<Manager> managers = new HashSet<>();
		private final List<Consumer<DiscordUtils<B>>> setup = new ArrayList<>();

		private LocalizationManager localization;

		private final JDABuilder jda;
		private final B bot;

		Builder(JDABuilder jda, B bot) {
			this.jda = jda;
			this.bot = bot;
		}

		@Override
		public Set<Manager> getManagers() {
			return managers;
		}

		/**
		 * @param stdout  Whether to mirror {@link System#out}
		 * @param stderr  Whether to mirror {@link System#err}
		 * @param targets The {@link RedirectTarget}s you want to mirror the console to
		 * @return {@code this}
		 */
		@NotNull
		public final Builder<B> mirrorConsole(boolean stdout, boolean stderr, @NotNull List<RedirectTarget<B>> targets) {
			Checks.notNull(targets, "targets");

			if(!stdout && !stderr) return this;
			if(targets.isEmpty()) return this;

			setup.add(0, discordUtils -> {
				var discordStreams = targets.stream().map(t -> new DiscordOutputStream(mes -> t.sendMessage(discordUtils, mes), 10)).toList();

				if(stdout) System.setOut(new MirrorPrintStream(discordStreams, System.out));
				if(stderr) System.setErr(new MirrorPrintStream(discordStreams, System.err));
			});

			return this;
		}

		/**
		 * @param targets The {@link RedirectTarget}s you want to mirror the console to
		 * @return {@code this}
		 */
		@NotNull
		public final Builder<B> mirrorConsole(@NotNull List<RedirectTarget<B>> targets) {
			return mirrorConsole(true, true, targets);
		}

		/**
		 * @param stdout  Whether to mirror {@link System#out}
		 * @param stderr  Whether to mirror {@link System#err}
		 * @param targets The {@link RedirectTarget}s you want to mirror the console to
		 * @return {@code this}
		 */
		@SafeVarargs
		@NotNull
		public final Builder<B> mirrorConsole(boolean stdout, boolean stderr, @NotNull RedirectTarget<B>... targets) {
			return mirrorConsole(stdout, stderr, Arrays.asList(targets));
		}

		/**
		 * @param targets The {@link RedirectTarget}s you want to mirror the console to
		 * @return {@code this}
		 */
		@SafeVarargs
		@NotNull
		public final Builder<B> mirrorConsole(@NotNull RedirectTarget<B>... targets) {
			return mirrorConsole(true, true, targets);
		}


		/**
		 * @param localization The {@link LocalizationManager} to use
		 * @return {@code this}
		 */
		@NotNull
		public Builder<B> setLocalizationManager(@Nullable LocalizationManager localization) {
			this.localization = localization;
			return this;
		}

		/**
		 * @param manager The {@link Manager} to add
		 * @param config  A function to configure the resulting manager
		 * @return {@code this}
		 */
		@NotNull
		public <T extends Manager> Builder<B> addManager(@NotNull T manager, @Nullable Consumer<T> config) {
			Checks.notNull(manager, "manager");

			jda.addEventListeners(manager);
			managers.add(manager);

			if(config != null) setup.add(discordUtils -> config.accept(manager));

			return this;
		}

		/**
		 * @param context      A function to create a context for events
		 * @param autocomplete A function to create a context for autocomplete
		 * @param config       A consumer to configure the resulting {@link CommandManager}
		 * @return {@code this}
		 */
		@NotNull
		public <C extends ICommandContext, A extends IAutocompleteContext> Builder<B> useCommandManager(@NotNull Function<GenericCommandInteractionEvent, ? extends C> context, @NotNull Function<CommandAutoCompleteInteractionEvent, ? extends A> autocomplete, @Nullable Consumer<CommandManager<C, A>> config) {
			Checks.notNull(context, "context");
			Checks.notNull(autocomplete, "autocomplete");

			return addManager(new CommandManager<>(this, context, autocomplete), config);
		}

		/**
		 * @return {@code this}
		 */
		@NotNull
		public Builder<B> useLanguageCache(@NotNull DiscordLocale defaultLocale) {
			return addManager(new LanguageCacheManager(defaultLocale), null);
		}

		/**
		 * @param config A consumer to configure the new {@link EventManager}
		 * @return {@code this}
		 */
		@NotNull
		public Builder<B> useEventManager(@Nullable Consumer<EventManager> config) {
			return addManager(new EventManager(), config);
		}


		/**
		 * @param config A consumer to configure the new {@link UIManager}
		 * @return {@code this}
		 */
		@NotNull
		public Builder<B> useUIManager(@Nullable Consumer<UIManager> config) {
			return addManager(new UIManager(this), config);
		}

		/**
		 * @param config A consumer to configure the new {@link ListManager}
		 * @return {@code this}
		 */
		@NotNull
		public <C extends ICommandContext> Builder<B> useListManager(@Nullable Consumer<ListManager<C>> config) {
			return addManager(new ListManager<>(this), config);
		}

		/**
		 * @param targets    The {@link HelpTarget} to add
		 * @param mainTarget The default {@link HelpTarget}
		 * @param config     A consumer to configure the new {@link HelpManager}
		 * @return {@code this}
		 */
		@NotNull
		public <C extends ICommandContext> Builder<B> useHelpManager(@NotNull Function<DiscordUtils.Builder<B>, List<? extends HelpTarget>> targets, @NotNull HelpTarget mainTarget, @Nullable Consumer<HelpManager<C>> config) {
			return addManager(new HelpManager<>(this, targets, mainTarget), config);
		}

		/**
		 * @param config A consumer to configure the new {@link CustomRestActionManager}
		 * @return {@code this}
		 */
		public Builder<B> useCustomRequests(@Nullable Consumer<CustomRestActionManager> config) {
			return addManager(new CustomRestActionManager(), config);
		}

		public DiscordUtils<B> build() {
			var result = new DiscordUtils<>(jda, bot, localization, managers);
			managers.forEach(m -> m.manager = result);
			Collections.reverse(setup);
			setup.forEach(x -> x.accept(result));
			return result;
		}
	}
}
