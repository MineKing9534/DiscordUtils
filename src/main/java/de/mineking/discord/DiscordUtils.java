package de.mineking.discord;

import de.mineking.discord.commandcache.CommandCacheManager;
import de.mineking.discord.commands.CommandImplementation;
import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.ContextBase;
import de.mineking.discord.commands.ContextCreator;
import de.mineking.discord.customrestaction.CustomRestActionManager;
import de.mineking.discord.events.EventManager;
import de.mineking.discord.help.HelpManager;
import de.mineking.discord.help.HelpTarget;
import de.mineking.discord.languagecache.LanguageCacheManager;
import de.mineking.discord.linkedroles.LinkedRolesManager;
import de.mineking.discord.linkedroles.MetaData;
import de.mineking.discord.list.ListManager;
import de.mineking.discord.localization.LocalizationManager;
import de.mineking.discord.oauth2.CredentialsManager;
import de.mineking.discord.oauth2.OAuth2Config;
import de.mineking.discord.oauth2.OAuth2Manager;
import de.mineking.discord.oauth2.data.OAuth2User;
import de.mineking.discord.ui.UIManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DiscordUtils {
	private final JDABuilder builder;
	private JDA jda;

	private final Object bot;
	private final Set<Module> modules = new HashSet<>();

	private final RestConfig restConfig = new RestConfig();

	private LocalizationManager localization = new LocalizationManager((info, locale) -> info.path, DiscordLocale.ENGLISH_US, Collections.emptyList());

	/**
	 * Creates a new {@link DiscordUtils} instance.
	 *
	 * @param bot An instance of your application's main class. You can access it later using {@link #getBot(Class)}.
	 * @param jda A {@link JDABuilder} with your configuration. Adding modules will further configure it. After registering all modules, you can use {@link #build()} wich will start the bot and return the resulting {@link JDA} instance.
	 */
	public DiscordUtils(@NotNull Object bot, @NotNull JDABuilder jda) {
		Checks.notNull(bot, "bot");
		Checks.notNull(jda, "jda");

		this.bot = bot;
		this.builder = jda;
	}

	/**
	 * @param type The type of your main class
	 * @return The instance of your main class that was provided to the constructor earlier
	 */
	@NotNull
	public <T> T getBot(@NotNull Class<T> type) {
		Checks.notNull(type, "type");
		return type.cast(bot);
	}

	/**
	 * @return Starts the bot and returns the resulting {@link JDA} instance
	 */
	@NotNull
	public JDA build() {
		var jda = builder
				.setRestConfig(restConfig)
				.build();

		modules.forEach(jda::addEventListener);
		this.jda = jda;

		return jda;
	}

	/**
	 * @return This managers {@link JDA} instance.
	 */
	@NotNull
	public JDA getJDA() {
		if(jda == null) {
			throw new IllegalStateException();
		}

		return jda;
	}

	/**
	 * Sets a new {@link LocalizationManager}.
	 *
	 * @param localization A {@link LocalizationManager}
	 * @return The same {@link DiscordUtils} instance
	 */
	@NotNull
	public DiscordUtils useLocalization(@NotNull LocalizationManager localization) {
		Checks.notNull(localization, "localization");
		this.localization = localization;
		return this;
	}

	/**
	 * @return The {@link LocalizationManager}
	 */
	@NotNull
	public LocalizationManager getLocalization() {
		return localization;
	}

	private <T extends Module> DiscordUtils addModule(T module, Consumer<T> handler) {
		modules.add(module);

		if(handler != null) {
			handler.accept(module);
		}

		return this;
	}

	/**
	 * @param type The type pf module you want to test
	 * @return Whether a module of the specified type is present
	 */
	public boolean hasModule(@NotNull Class<? extends Module> type) {
		Checks.notNull(type, "type");
		return getModule(type).isPresent();
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> Optional<T> getModule(Class<T> type) {
		return modules.stream()
				.filter(m -> m.getClass().equals(type))
				.findFirst().map(m -> (T) m);
	}

	public <C extends ContextBase> DiscordUtils useCommandManager(ContextCreator<C> context, Consumer<CommandManager<C>> handler) {
		return addModule(new CommandManager<>(this, context), handler);
	}

	public CommandManager<?> getCommandManager() {
		return getModule(CommandManager.class).orElseThrow(() -> new IllegalStateException("No CommandManager registered"));
	}

	public DiscordUtils useEventManager(Consumer<EventManager> handler) {
		return addModule(new EventManager(this), handler);
	}

	public EventManager getEventManager() {
		return getModule(EventManager.class).orElseThrow(() -> new IllegalStateException("No EventManager registered"));
	}

	public DiscordUtils useUIManager(Consumer<UIManager> handler) {
		if(!hasModule(EventManager.class)) {
			throw new IllegalStateException("The UIManager module requires the EventManager module");
		}

		return addModule(new UIManager(this), handler);
	}

	public UIManager getUIManager() {
		return getModule(UIManager.class).orElseThrow(() -> new IllegalStateException("No UIManager registered"));
	}

	public DiscordUtils useListCommands(Consumer<ListManager> handler) {
		return addModule(new ListManager(this), handler);
	}

	public ListManager getListManager() {
		return getModule(ListManager.class).orElseThrow(() -> new IllegalStateException("No ListManager registered"));
	}

	public DiscordUtils useCommandCache(Consumer<CommandCacheManager> handler) {
		if(!hasModule(CommandManager.class)) {
			throw new IllegalStateException("The CommandCache module requires the CommandManager module");
		}

		return addModule(new CommandCacheManager(this), handler);
	}

	public CommandCacheManager getCommandCache() {
		return getModule(CommandCacheManager.class).orElseThrow(() -> new IllegalStateException("No CommandCache registered"));
	}

	public DiscordUtils useLanguageCache(DiscordLocale defaultLocale, Consumer<LanguageCacheManager> handler) {
		return addModule(new LanguageCacheManager(this, defaultLocale), handler);
	}

	public LanguageCacheManager getLanguageCache() {
		return getModule(LanguageCacheManager.class).orElseThrow(() -> new IllegalStateException("No LanguageCache registered"));
	}

	public DiscordUtils useHelpManager(boolean helpSubcommands, HelpTarget mainPage, Function<CommandImplementation, HelpTarget> commandTarget, Consumer<HelpManager> config) {
		if(!hasModule(CommandManager.class)) {
			throw new IllegalStateException("The HelpManager module requires the CommandManager module");
		}

		return addModule(new HelpManager(this, helpSubcommands, mainPage, commandTarget), config);
	}

	public HelpManager getHelpManager() {
		return getModule(HelpManager.class).orElseThrow(() -> new IllegalStateException("No HelpManager registered"));
	}

	public DiscordUtils useOAuth2Manager(OAuth2Config config, CredentialsManager credentialsManager, Consumer<OAuth2Manager> handler) {
		var oldBuilder = restConfig.getCustomBuilder();

		restConfig.setCustomBuilder(
				b -> {
					if(oldBuilder != null) {
						oldBuilder.accept(b);
					}

					var oauth2 = b.build().header("du-oauth2");

					if(oauth2 != null) {
						b.header("Authorization", oauth2).removeHeader("du-oauth2");
					}
				}
		);

		return addModule(new OAuth2Manager(this, config, credentialsManager), handler);
	}

	public OAuth2Manager getOAuth2Manager() {
		return getModule(OAuth2Manager.class).orElseThrow(() -> new IllegalStateException("No OAuth2Manager registered"));
	}

	public DiscordUtils useLinkedRolesManager(String endpoint, String successUrl, BiFunction<OAuth2User, MetaData, Object> handler, Consumer<LinkedRolesManager> config) {
		if(!hasModule(OAuth2Manager.class)) {
			throw new IllegalStateException("The LinkedRolesManager module requires the OAuth2Manager module");
		}

		return addModule(new LinkedRolesManager(this, endpoint, successUrl, handler), config);
	}

	public LinkedRolesManager getLinkedRolesManager() {
		return getModule(LinkedRolesManager.class).orElseThrow(() -> new IllegalStateException("No LinkedRolesManager registered"));
	}

	public DiscordUtils useCustomRestactionManager(Consumer<CustomRestActionManager> handler) {
		var oldBuilder = restConfig.getCustomBuilder();

		restConfig.setCustomBuilder(b -> {
			if(oldBuilder != null) {
				oldBuilder.accept(b);
			}

			var temp = b.build();
			var host = temp.header("du-host");

			if(host != null) {
				b.url(HttpUrl.parse(host).newBuilder()
						.addPathSegments(temp.header("du-route"))
						.build()
				).removeHeader("du-host").removeHeader("du-route");
			}
		});

		return addModule(new CustomRestActionManager(this), handler);
	}

	public CustomRestActionManager getCustomRestActionManager() {
		return getModule(CustomRestActionManager.class).orElseThrow(() -> new IllegalStateException("No CustomRestActionManager registered"));
	}

	/**
	 * Shutdown this {@link DiscordUtils}. This will not shut down the {@link JDA} instance but only cleanup all modules
	 */
	public void shutdown() {
		modules.forEach(Module::cleanup);
	}
}
