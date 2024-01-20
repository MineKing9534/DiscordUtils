package de.mineking.discordutils.restaction;

import net.dv8tion.jda.api.requests.Route;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public record CustomRequest<T>(@NotNull CustomRestAction<T> action, @NotNull Route.CompiledRoute route,
							   @Nullable Consumer<? super T> onSuccess, @Nullable Consumer<? super Throwable> onError,
							   @Nullable RequestBody body, @Nullable CaseInsensitiveMap<String, String> headers,
							   @Nullable BooleanSupplier check) {
	void handleSuccess(@NotNull Response response) throws IOException {
		if(response.isSuccessful()) action.handleSuccess(this, response);
		else if(onError != null) onError.accept(new HttpException(response));
	}

	void handleError(@NotNull Exception e) {
		if(onError != null) onError.accept(e);
		else CustomRestActionManager.logger.error("Custom RestAction failed", e);
	}
}
