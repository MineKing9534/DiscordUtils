package de.mineking.discordutils.restaction;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HttpErrorHandler implements Consumer<Throwable> {
	private final static Consumer<Throwable> empty = e -> {};

	private final Consumer<Throwable> base;
	private final Map<Predicate<? extends Throwable>, Consumer<? extends Throwable>> cases = new LinkedHashMap<>();

	public HttpErrorHandler(@NotNull Consumer<Throwable> base) {
		Checks.notNull(base, "base");
		this.base = base;
	}

	public HttpErrorHandler() {
		this(e -> CustomRestActionManager.logger.error("Custom RestAction failed", e));
	}

	@SafeVarargs
	@NotNull
	public final HttpErrorHandler ignore(@NotNull Class<?> clazz, @NotNull Class<? extends Throwable>... classes) {
		Checks.notNull(clazz, "clazz");
		Checks.notNull(classes, "classes");

		return ignore(e -> {
			if(clazz.isInstance(e)) return true;
			return Arrays.stream(classes).anyMatch(c -> c.isInstance(e));
		});
	}

	@NotNull
	public HttpErrorHandler ignore(@NotNull Predicate<? extends Throwable> predicate) {
		Checks.notNull(predicate, "predicate");
		cases.put(predicate, empty);
		return this;
	}

	@NotNull
	public HttpErrorHandler handle(@NotNull Predicate<? extends Throwable> predicate, @NotNull Consumer<? extends Throwable> handler) {
		Checks.notNull(predicate, "predicate");
		Checks.notNull(handler, "handler");

		cases.put(predicate, handler);

		return this;
	}

	@NotNull
	public <T extends Throwable> HttpErrorHandler handle(@NotNull Class<T> clazz, @NotNull Consumer<T> handler) {
		Checks.notNull(clazz, "clazz");
		return handle(clazz::isInstance, handler);
	}

	@Override
	public void accept(Throwable throwable) {
		for(var e : cases.entrySet()) if(handle(e.getKey(), e.getValue(), throwable)) return;
		base.accept(throwable);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private boolean handle(Predicate test, Consumer handler, Throwable throwable) {
		if(test.test(throwable)) {
			handler.accept(throwable);
			return true;
		}

		return false;
	}
}
