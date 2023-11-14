package de.mineking.discordutils.commands.option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface OptionArray {
	/**
	 * The required number of options
	 */
	int minCount();

	/**
	 * The total number of options
	 */
	int maxCount();
}
