package de.mineking.discord.commands.interaction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.CommandPermission;
import de.mineking.discord.commands.history.ExecutionData;
import de.mineking.discord.commands.interaction.context.Context;
import de.mineking.exceptions.Checks;
import de.mineking.utils.ReflectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class Command<T extends GenericCommandInteractionEvent, C extends Context<T>> {
	String name;
	Feature feature;
	
	/**
	 * A member has to have at least this permission to perform this command
	 */
	protected CommandPermission permission;
	
	/**
	 * How this command should be acknowledged by default.
	 * 
	 * <ul>
	 * 	<li>null: Don't acknowledge</li>
	 * 	<li>true: Acknowledge ephemeral</li>
	 * 	<li>false: Acknowledge non-ephemeral</li>
	 * </ul>
	 * 
	 * default: true
	 */
	protected Boolean defaultAcknowledge;
	
	
	private final Class<T> type;
	
	
	public Command(Class<T> type) {
		this.type = type;
		
		permission = null;
		defaultAcknowledge = true;
	}
	
	public final void run(@Nonnull ExecutionData<T, C> data) {
		Checks.nonNull(data, "data");

		if(type.isAssignableFrom(data.event.getClass())) {
			performCommand(buildContext(data));
		}
	}
	
	protected abstract C buildContext(ExecutionData<T, C> data);
	
	protected void performCommand(C context) {}
	
	/**
	 * @return Whether the command action should be acknowledged by default.
	 */
	public final Boolean defaultAcknowledge() {
		return defaultAcknowledge;
	}
	
	/**
	 * Builds this command and converts it to CommandData
	 * 
	 * @param g
	 * 		The Guild you want to build the command for. 
	 * 
	 * @return The resulting CommandData
	 */
	@Nullable
	public abstract CommandData build(@Nullable Guild g);
	
	/**
	 * @return The path of this command. This includes the feature, the command name and subcommands if present. All of these are separated by '.'
	 */
	@Nonnull
	public String getPath() {
		return feature.getName() + "." + name;
	}
	
	/**
	 * @return The name of this command. Will be set through adding it to a feature.
	 */
	@Nonnull
	public final String getName() {
		if(name != null) {
			return name;
		}
		
		else {
			throw new IllegalStateException("This command hasn't a name yet. Add it to a feature to do it.");
		}
	}
	
	/**
	 * @return This command as SlashCommand, or null if it isn't a slash command
	 */
	@Nullable
	public final SlashCommand getAsSlash() {
		return (this instanceof SlashCommand sc ? sc : null);
	}
	
	/**
	 * @return This command as ContextCommand, or null if it isn't a context command
	 */
	@Nullable
	public final ContextCommand<T, C> getAsContext() {
		return (this instanceof ContextCommand<T, C> cc ? cc : null);
	}
	
	/**
	 * @return The Feature, this command was added to
	 */
	@Nonnull
	public Feature getFeature() {
		if(feature == null) {
			throw new IllegalStateException("This comman wasn't added to a feature yet!");
		}
		
		return feature;
	}
	
	/**
	 * @return The permission needed, to perform this command
	 */
	@Nonnull
	public CommandPermission getPermission() {
		return permission != null ? permission : feature.getManager().getEveryonePermission();
	}
	
	/**
	 * @return A clone of this Command
	 */
	@SuppressWarnings("unchecked")
	@Nonnull
	public Command<T, C> createClone() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<? extends Command<T, C>> c = (Constructor<? extends Command<T, C>>)getClass().getDeclaredConstructor();
		c.setAccessible(true);
		
		Command<T, C> cmd = c.newInstance();
		
		for(Field f : ReflectionUtils.getFields(cmd.getClass())) {
			f.setAccessible(true);
			
			if(!Modifier.isStatic(f.getModifiers())) {
				f.set(cmd, f.get(this));
			}
		}
		
		if(cmd instanceof SlashCommand sc) {
			SlashCommand.updateSubcommands(sc);
		}
		
		return cmd;
	}
}
