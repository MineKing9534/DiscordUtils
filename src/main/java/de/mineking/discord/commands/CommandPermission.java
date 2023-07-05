package de.mineking.discord.commands;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public interface CommandPermission {
	default boolean isPermitted(CommandManager<?> manager, GenericInteractionCreateEvent event) {
		return true;
	}

	default void handleUnpermitted(CommandManager<?> manager, GenericCommandInteractionEvent event) {}

	default DefaultMemberPermissions requirePermissions() {
		return DefaultMemberPermissions.ENABLED;
	}
}
