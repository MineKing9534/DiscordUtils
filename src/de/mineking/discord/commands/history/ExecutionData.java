package de.mineking.discord.commands.history;

import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.interaction.Command;
import de.mineking.discord.commands.interaction.context.Context;
import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class ExecutionData<T extends GenericCommandInteractionEvent, C extends Context<T>> extends RuntimeData {
	protected Command<T, C> cmd;
	protected boolean isPermitted;
	
	public ExecutionData(@Nonnull RuntimeData data, @Nonnull Command<T, C> cmd, boolean isPermitted) {
		super(data);
		
		Checks.nonNull(cmd, "cmd");
		
		this.cmd = cmd;
		this.isPermitted = isPermitted;
	}
	
	/**
	 * @return The executed command
	 */
	@Nonnull
	public Command<T, C> getCommand() {
		return cmd;
	}
	
	/**
	 * @return Whether the execution was allowed
	 */
	public boolean isPermitted() {
		return isPermitted;
	}
	
	/**
	 * @return A string representation of the arguments and theirs values
	 */
	@Nonnull
	public String getArgsString() {
		return event.getOptions().stream()
				.map(o -> o.getName() + ": " + mapOptionToString(o))
				.collect(Collectors.joining(" "));
	}
	
	private String mapOptionToString(OptionMapping om) {
		String result = "";
		
		switch(om.getType()) {
			case BOOLEAN:
				result = String.valueOf(om.getAsBoolean());
				break;
			case CHANNEL:
				result = om.getAsGuildChannel().getAsMention();
				break;
			case INTEGER:
				result = String.valueOf(om.getAsLong());
				break;
			case ROLE:
				result = om.getAsRole().getAsMention();
				break;
			case STRING:
				result = om.getAsString();
				break;
			case USER:
				result = om.getAsUser().getAsMention();
				break;
			case MENTIONABLE:
				result = om.getAsMentionable().getAsMention();
				break;
			case SUB_COMMAND: break;
			case SUB_COMMAND_GROUP: break;
			case UNKNOWN: break;
			default: break;
		}
		
		return result;
	}
	
	/**
	 * @return A String representation of the command call
	 */
	@Override
	@Nonnull
	public String toString() {
		return path.replace("/", " ") + (getArgsString().isEmpty() ? "" : " " + getArgsString());
	}
}
