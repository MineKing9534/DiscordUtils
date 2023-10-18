package de.mineking.discord.languagecache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.mineking.discord.DiscordUtils;
import de.mineking.discord.Module;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.concurrent.TimeUnit;

public class LanguageCacheManager extends Module {
	private final Cache<Long, DiscordLocale> cache = Caffeine.newBuilder()
			.expireAfterWrite(2, TimeUnit.HOURS)
			.build();

	private final DiscordLocale defaultLocale;

	public LanguageCacheManager(DiscordUtils manager, DiscordLocale defaultLocale) {
		super(manager);

		this.defaultLocale = defaultLocale;
	}

	public DiscordLocale getLocale(UserSnowflake user) {
		return cache.asMap().getOrDefault(user.getIdLong(), defaultLocale);
	}

	@Override
	public void onGenericInteractionCreate(GenericInteractionCreateEvent event) {
		cache.put(event.getUser().getIdLong(), event.getUserLocale());
	}
}
