package de.mineking.discordutils;

import java.io.IOException;

public interface IOBiFunction<T, U, R> {
	R apply(T t, U u) throws IOException;
}
