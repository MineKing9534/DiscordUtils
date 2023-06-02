package de.mineking.discord.ui;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class CallbackState {
	public IReplyCallback reply;
	public IModalCallback modal;

	public CallbackState(IReplyCallback reply, IModalCallback modal) {
		this.reply = reply;
		this.modal = modal;
	}

	public CallbackState(IReplyCallback reply) {
		this(reply, null);
	}

	public CallbackState(IModalCallback modal) {
		this(null, modal);
	}

	public CallbackState(GenericCommandInteractionEvent event) {
		this(event, event);
	}

	public CallbackState(GenericComponentInteractionCreateEvent event) {
		this(event, event);
	}
}
