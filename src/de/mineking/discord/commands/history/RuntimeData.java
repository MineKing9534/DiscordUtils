package de.mineking.discord.commands.history;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.CommandManager;
import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class RuntimeData extends CommandData {
	protected CommandManager cmdMan;
	
	protected Member m;
	protected GuildMessageChannel channel;
	
	protected Map<String, OptionMapping> args;
	
	protected GenericCommandInteractionEvent event;
	
	public RuntimeData(RuntimeData data) {
		this(data.getManager(), data.getEvent());
	}

	public RuntimeData(@Nonnull CommandManager cmdMan, @Nonnull GenericCommandInteractionEvent event) {
		super(event);
		
		Checks.nonNull(cmdMan, "cmdMan");
		
		this.cmdMan = cmdMan;
		this.event = event;
		
		this.channel = (GuildMessageChannel)event.getMessageChannel();
		this.m = event.getMember();
		
		this.args = new HashMap<>();
		
		if(event instanceof SlashCommandInteractionEvent) {
			for(OptionMapping om : ((SlashCommandInteractionEvent)event).getOptions()) {
				this.args.put(om.getName(), om);
			}
		}
	}
	
	/**
	 * @return The GenericCommandInteractionEvent attached to this execution
	 */
	@Nonnull
	public GenericCommandInteractionEvent getEvent() {
		return event;
	}
	
	/**
	 * @return The CommandManager this command is handled by
	 */
	@Nonnull
	public CommandManager getManager() {
		return cmdMan;
	}
	
	/**
	 * @return The member performing this command
	 */
	@Nonnull
	public Member getMember() {
		return m;
	}
	
	/**
	 * @return The guild this command was performed on
	 */
	@Nonnull
	public Guild getGuild() {
		return channel.getGuild();
	}
	
	/**
	 * @return The channel this command was performed in
	 */
	@Nonnull
	public GuildMessageChannel getChannel() {
		return channel;
	}

	/**
	 * @return The arguments for the execution
	 */
	@Nonnull
	public Map<String, OptionMapping> getArgs() {
		return args;
	}
}
