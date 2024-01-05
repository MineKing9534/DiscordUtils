package de.mineking.discordutils.console;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.SplitUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DiscordOutputStream extends OutputStream {
	public final static int additionalLength = "```ansi\n```".length() + 30;

	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private final Consumer<MessageCreateBuilder> handler;
	private final StringBuffer buffer = new StringBuffer();

	private Instant last = Instant.now();

	/**
	 * @param handler The handler for messages that can be sent
	 * @param delay   The update delay in seconds
	 */
	public DiscordOutputStream(@NotNull Consumer<MessageCreateBuilder> handler, int delay) {
		Checks.notNull(handler, "handler");
		this.handler = handler;

		executor.scheduleAtFixedRate(() -> {
			if(buffer.isEmpty()) return;
			if(last.plus(Duration.ofSeconds(delay)).isAfter(Instant.now())) return;

			send(true);
		}, 0, delay, TimeUnit.SECONDS);
	}

	private synchronized void send(boolean sendIncomplete) {
		synchronized(buffer) {
			for(var s : SplitUtil.split(buffer.toString(), Message.MAX_CONTENT_LENGTH - additionalLength, SplitUtil.Strategy.NEWLINE, SplitUtil.Strategy.WHITESPACE, SplitUtil.Strategy.ANYWHERE)) {
				if(!sendIncomplete && s.length() < Message.MAX_CONTENT_LENGTH - additionalLength) return;

				handler.accept(new MessageCreateBuilder().setContent(MarkdownUtil.codeblock("ansi", s)));
				buffer.delete(0, s.length());
			}

			last = Instant.now();
		}
	}

	@Override
	public synchronized void write(int b) {
		synchronized(buffer) {
			buffer.append((char) b);

			if(buffer.length() > Message.MAX_CONTENT_LENGTH - additionalLength) send(false);
		}
	}

	@Override
	public void close() {
		executor.shutdownNow();
	}
}
