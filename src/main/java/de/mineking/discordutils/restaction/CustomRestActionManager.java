package de.mineking.discordutils.restaction;

import de.mineking.discordutils.Manager;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomRestActionManager extends Manager {
	public final static Logger logger = LoggerFactory.getLogger(CustomRestActionManager.class);

	final ExecutorService executor = Executors.newScheduledThreadPool(0);
	final OkHttpClient client;

	public CustomRestActionManager() {
		client = new OkHttpClient();
	}

	@NotNull
	public HttpHost createHost(@NotNull String url) {
		Checks.notNull(url, "url");
		return new HttpHost(this, url);
	}
}
