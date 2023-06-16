package de.mineking.discord.commands.annotated;

import de.mineking.discord.commands.CommandImplementation;
import de.mineking.discord.commands.CommandInfo;
import de.mineking.discord.commands.CommandPermission;
import net.dv8tion.jda.api.entities.Guild;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Function;

public class ReflectionCommandImplementationBase extends CommandImplementation {
	public ReflectionCommandImplementationBase(CommandImplementation parent, Set<CommandImplementation> children, CommandInfo info, Class<?> type, Function<Object, Object> instance) {
		super(parent, children, info, type, instance);
	}

	@Override
	public boolean addToGuild(Guild guild) {
		try {
			for(var m : type.getMethods()) {
				if(m.isAnnotationPresent(GuildCommandPredicate.class)) {
					return (boolean) m.invoke(instance.apply(null), guild);
				}
			}
		} catch(IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public CommandPermission getPermission() {
		for(var f : type.getFields()) {
			if(CommandPermission.class.isAssignableFrom(f.getType())) {
				try {
					return (CommandPermission) f.get(instance.apply(null));
				} catch(IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return null;
	}
}
