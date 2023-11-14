package de.mineking.discordutils.console;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;

public class MirrorPrintStream extends PrintStream {
	public MirrorPrintStream(@NotNull Collection<? extends OutputStream> mirror1, @NotNull OutputStream... mirror2) {
		super(new MirrorOutputStream(mirror1, mirror2));
	}

	public MirrorPrintStream(@NotNull OutputStream... mirror) {
		super(new MirrorOutputStream(mirror));
	}
}
