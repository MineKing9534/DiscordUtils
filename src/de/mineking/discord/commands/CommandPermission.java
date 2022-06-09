package de.mineking.discord.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public interface CommandPermission {
	/**
	 * Use this to map this CommadPermission to a discord role
	 * 
	 * @param g
	 * 		The guild
	 * 
	 * @return
	 * 		The discord role for this guild
	 */
	public default Role getRole(Guild g) {
		return g.getPublicRole();
	}
}
