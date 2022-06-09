package de.mineking.discord.commands.interaction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.CommandPermission;
import de.mineking.discord.commands.history.RuntimeData;
import de.mineking.discord.commands.interaction.context.ContextCommand;
import de.mineking.discord.commands.reply.ModalReplyAction;
import de.mineking.discord.commands.reply.ReplyManager;
import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.RestAction;

public abstract class Command {
	private RuntimeData execData;
	private ReplyManager replyManager;
	
	
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
	
	
	public Command() {
		permission = null;
		defaultAcknowledge = true;
	}
	
	public final void run(@Nonnull RuntimeData data) {
		Checks.nonNull(data, "data");
		
		this.execData = data;
		this.replyManager = new ReplyManager(data);
		
		perform();
	}
	
	public abstract void perform();
	
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
		return (this instanceof SlashCommand ? (SlashCommand)this : null);
	}
	
	/**
	 * @return This command as ContextCommand, or null if it isn't a context command
	 */
	@Nullable
	public final ContextCommand getAsContext() {
		return (this instanceof ContextCommand ? (ContextCommand)this : null);
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
	 * This method will only work in execution instances create by the CommandManager!
	 * 
	 * @return The ExecutionData attached to this execution instance
	 */
	@Nonnull
	protected final RuntimeData getRuntimeData() {
		if(execData != null) {
			return execData;
		}
		
		else {
			throw new IllegalStateException("You cannot reply to a non-executed command!");
		}
	}
	
	/**
	 * This method will only work in execution instances create by the CommandManager!
	 * 
	 * @return The ReplyManager attached to this execution instance
	 */
	@Nonnull
	protected final ReplyManager getReplyManager() {
		if(replyManager != null) {
			return replyManager;
		}
		
		else {
			throw new IllegalStateException("You cannot reply to a non-executed command!");
		}
	}
	
	/**
	 * This method will only work in execution instances create by the CommandManager!
	 * 
	 * @return The GenericCommandInteractionEvent attached to this execution instance
	 */
	@Nonnull
	protected final GenericCommandInteractionEvent getEvent() {
		return getRuntimeData().getEvent();
	}
	
	/**
	 * Replies to the Command
	 * <b>WARNING:</b> This does only work for acknowledged interactions. To use this enable the defaultAcknowledge field or acknowledge the interaction yourself!
	 * 
	 * @param mes
	 * 		The Message you want to reply
	 * 
	 * @return A MessageReplyAction for handling the sending
	 */
	@Nonnull
	protected final RestAction<Message> reply(@Nonnull Message mes) {
		Checks.nonNull(mes, "mes");
		
		if(getReplyManager() != null) {
			return getReplyManager().reply(mes);
		}
		
		else {
			throw new IllegalStateException();
		}
	}
	
	/**
	 * Replies to the command
	 * <b>WARNING:</b> This does only work for acknowledged interactions. To use this enable the defaultAcknowledge field or acknowledge the interaction yourself!
	 * 
	 * @param message
	 * 		The String to reply
	 * 
	 * @return A MessageReplyAction for handling the sending
	 */
	@Nonnull
	protected final RestAction<Message> reply(@Nonnull String message) {
		Checks.nonNull(message, "message");
		
		return reply(new MessageBuilder()
				.setContent(message)
				.build());
	}
	
	/**
	 * Replies to the command
	 * <b>WARNING:</b> This does only work for acknowledged interactions. To use this enable the defaultAcknowledge field or acknowledge the interaction yourself!
	 * 
	 * @param embed
	 * 		The MessageEmbed to reply
	 * 
	 * @return A MessageReplyAction for handling the sending
	 */
	@Nonnull
	protected final RestAction<Message> reply(@Nonnull MessageEmbed embed) {
		Checks.nonNull(embed, "embed");
		
		return reply(new MessageBuilder()
				.setEmbeds(embed)
				.build());
	}
	
	/**
	 * Replies a modal to the command
	 * 
	 * @param modal
	 * 		The modal to reply
	 * 
	 * @return A ModalReplyAction for handling the sending
	 */
	@Nonnull
	protected final ModalReplyAction reply(@Nonnull Modal modal) {
		Checks.nonNull(modal, "modal");
		
		if(getReplyManager() != null) {
			return getReplyManager().reply(modal);
		}
		
		else {
			throw new IllegalStateException();
		}
	}

	
	private static List<Field> getAllFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		
		do {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			
			clazz = clazz.getSuperclass();
		} while(clazz != null);
		
		return fields;
	}
	
	/**
	 * @return A clone of this Command
	 */
	@Nonnull
	public Command createClone() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<? extends Command> c = getClass().getDeclaredConstructor();
		c.setAccessible(true);
		
		Command cmd = c.newInstance();
		
		for(Field f : getAllFields(cmd.getClass())) {
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
