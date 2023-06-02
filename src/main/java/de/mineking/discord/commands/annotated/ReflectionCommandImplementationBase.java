package de.mineking.discord.commands.annotated;

import de.mineking.discord.commands.CommandImplementation;
import de.mineking.discord.commands.CommandInfo;
import de.mineking.discord.commands.CommandPermission;
import net.dv8tion.jda.api.entities.Guild;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ReflectionCommandImplementationBase extends CommandImplementation {
	public ReflectionCommandImplementationBase(CommandImplementation parent, Set<CommandImplementation> children, CommandInfo info, Object instance) {
		super(parent, children, info, instance);
	}

	@Override
	public boolean addToGuild(Guild guild) {
		try {
			for(var m : instance.getClass().getMethods()) {
				if(m.isAnnotationPresent(GuildCommandPredicate.class)) {
					return (boolean) m.invoke(instance, guild);
				}
			}
		} catch(IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public CommandPermission getPermission() {
		for(var f : instance.getClass().getFields()) {
			if(CommandPermission.class.isAssignableFrom(f.getType())) {
				try {
					return (CommandPermission) f.get(instance);
				} catch(IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return null;
	}
}
