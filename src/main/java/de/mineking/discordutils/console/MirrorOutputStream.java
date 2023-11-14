package de.mineking.discordutils.console;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * An {@link OutputStream} implementation that mirrors it's output to multiple other {@link OutputStream}s
 */
public class MirrorOutputStream extends OutputStream {
	private final Collection<OutputStream> mirror;

	public MirrorOutputStream(@NotNull Collection<? extends OutputStream> mirror1, @NotNull OutputStream... mirror2) {
		Checks.notNull(mirror1, "mirror1");
		Checks.notNull(mirror2, "mirror2");

		this.mirror = new ArrayList<>(mirror1);
		mirror.addAll(Arrays.asList(mirror2));
	}

	public MirrorOutputStream(@NotNull OutputStream... mirror) {
		this(Collections.emptyList(), mirror);
	}

	@Override
	public void write(int b) throws IOException {
		for(var o : mirror) o.write(b);
	}

	@Override
	public void flush() throws IOException {
		for(var o : mirror) o.flush();
	}

	@Override
	public void close() throws IOException {
		for(var o : mirror) o.close();
	}
}
