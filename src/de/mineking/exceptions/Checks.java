package de.mineking.exceptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Checks {
	public static void nonNull(@Nullable Object obj, @Nonnull String name) {
		if(name != null) {
			if(obj == null) {
				throw new InvalidNullArgumentException(name);
			}
		}
		
		else {
			throw new InvalidNullArgumentException("name");
		}
	}
}
