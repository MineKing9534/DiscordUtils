package de.mineking.discord.commands.interaction.handler;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public abstract class InteractionHandler <Event extends GenericInteractionCreateEvent, Argument> {
	private boolean autoRemove;
	
	private List<User> users;
	
	/**
	 * @param autoRemove
	 * 		Whether the handler should be removed after one use automatically
	 */
	public InteractionHandler(boolean autoRemove) {
		this.autoRemove = autoRemove;
		
		users = null;
	}
	
	/**
	 * @return Whether autoRemove is enabled
	 */
	public boolean autoRemove() {
		return autoRemove;
	}
	
	public void addUser(User u) {
		if(users == null) {
			users = new ArrayList<>();
		}
		
		users.add(u);
	}
	
	protected abstract void run(Event event, Argument args);
	
	protected abstract Argument getArguments(Event event);
	
	public final boolean execute(@Nonnull Event event) {
		Checks.notNull(event, "event");
		
		if(users != null && !users.contains(event.getUser())) {
			return false;
		}
		
		run(event, getArguments(event));
		
		return true;
	}
}
