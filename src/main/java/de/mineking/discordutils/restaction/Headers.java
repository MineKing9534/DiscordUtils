package de.mineking.discordutils.restaction;

import net.dv8tion.jda.internal.utils.Checks;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Headers {
	final CaseInsensitiveMap<String, String> headers = new CaseInsensitiveMap<>();

	/**
	 * Adds a header
	 *
	 * @param name  The name of the headers
	 * @param value The value of the header
	 * @return {@code this}
	 */
	@NotNull
	public Headers with(@NotNull String name, @Nullable String value) {
		Checks.notNull(name, "name");
		if(value != null) headers.put(name, value);
		else headers.remove(name);

		return this;
	}
}
