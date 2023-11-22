package de.mineking.discordutils.languagecache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.mineking.discordutils.Manager;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;

public class LanguageCacheManager extends Manager {
	private final DiscordLocale defaultLocale;

	private final Cache<Long, DiscordLocale> cache = Caffeine.newBuilder()
			.expireAfterAccess(Duration.ofHours(4))
			.build();

	public LanguageCacheManager(@NotNull DiscordLocale defaultLocale) {
		Checks.notNull(defaultLocale, "defaultLocale");
		this.defaultLocale = defaultLocale;
	}

	/**
	 * @param user The user to get the cached locale from
	 * @return The cached {@link DiscordLocale} for the specified user or the default locale
	 */
	@NotNull
	public DiscordLocale getLocale(@NotNull UserSnowflake user) {
		Checks.notNull(user, "user");
		return Optional.ofNullable(cache.getIfPresent(user.getIdLong())).orElse(defaultLocale);
	}

	@Override
	public void onGenericInteractionCreate(@NotNull GenericInteractionCreateEvent event) {
		cache.put(event.getUser().getIdLong(), event.getUserLocale());
	}
}
