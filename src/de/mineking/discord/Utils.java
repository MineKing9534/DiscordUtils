package de.mineking.discord;

import java.util.regex.Matcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.RestAction;

public class Utils {
	/**
	 * Checks if a member has a specific role.
	 * 
	 * @param m
	 * 		The Member
	 * 
	 * @param role
	 * 		The role Role
	 * 
	 * @return true if the member has the specific role, false otherwise or if the role is null.
	 */
	public static boolean hasRole(@Nonnull Member m, @Nullable Role role) {
		Checks.nonNull(m, "m");
		
		return (m.getPermissions().contains(Permission.ADMINISTRATOR) || (role != null && (m.getRoles().contains(role) || role.equals(role.getGuild().getPublicRole()))));
	}
	
	/**
	 * Gets a message by its jump url
	 * 
	 * @param url
	 * 		The jump url of the message
	 * 
	 * @param jda
	 * 		The JDA instance
	 * 
	 * @return A RestAction for the message retrieval
	 */
	public static RestAction<Message> getByJumpUrl(@Nonnull String url, @Nonnull JDA jda) throws IllegalArgumentException {
		Checks.nonNull(url, "url");
		Checks.nonNull(jda, "jda");
		
		Matcher m = Message.JUMP_URL_PATTERN.matcher(url);
		
		if(!m.matches()) {
			throw new IllegalArgumentException("Invalid url");
		}
		
		Guild guild = jda.getGuildById(m.group("guild"));
		
		if(guild == null) {
			throw new IllegalArgumentException("Unknown message");
		}
		
		MessageChannel channel = guild.getChannelById(MessageChannel.class, m.group("channel"));
		
		if(channel == null) {
			throw new IllegalArgumentException("Unknown message");
		}
		
		return channel.retrieveMessageById(m.group("message"));
	}
	
	/**
	 * Shortcut for label(str, 25). 
	 */
	public static String label(@Nonnull String str) {
		return label(str, 25);
	}
	
	/**
	 * Cuts a string to the maximal length and replaces the last 3 characters with ... if the current length if bigger than the max
	 * 
	 * @param str
	 * 		The input string
	 * 
	 * @param max
	 * 		The maximal length of the string
	 * 
	 * @return The resulting string
	 */
	public static String label(@Nonnull String str, int max) {
		Checks.nonNull(str, "str");
		
		return str.length() > max ? str.substring(0, max - 3) + "..." : str;
	}
}
