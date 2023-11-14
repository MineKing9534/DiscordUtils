package de.mineking.discordutils.commands.option;

import de.mineking.discordutils.commands.CommandManager;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * A {@link IOptionParser} implementation that can be used for simple custom option types
 */
public abstract class OptionParser implements IOptionParser {
	private final Class<?> clazz;
	private final OptionType type;

	/**
	 * @param clazz The java type that your parser returns
	 * @param type  The {@link OptionType} that your custom type should use
	 */
	public OptionParser(Class<?> clazz, OptionType type) {
		this.clazz = clazz;
		this.type = type;
	}

	@Override
	public boolean accepts(@NotNull Class<?> type) {
		return type.isAssignableFrom(clazz);
	}

	@NotNull
	@Override
	public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
		return this.type;
	}
}
