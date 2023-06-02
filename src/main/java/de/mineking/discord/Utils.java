package de.mineking.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * A utils class containing some simple but useful methods for bot developing with JDA.
 */
public class Utils {
	/**
	 * @param member A {@link Member} to check
	 * @param role   A {@link Role}
	 * @return Whether the member has the specified role. This will return {@code true} for sure if the member has {@link Permission#ADMINISTRATOR} permissions and requires the member to be an administrator if the role is {@code null}.
	 */
	public static boolean hasRole(@NotNull Member member, @Nullable Role role) {
		Checks.notNull(member, "member");

		return member.hasPermission(Permission.ADMINISTRATOR) || (role != null && (role.isPublicRole() || member.getRoles().contains(role)));
	}

	/**
	 * @param url The message jump url
	 * @param jda The {@link JDA} instance of your bot
	 * @return A {@link RestAction} to retrieve the message
	 * @throws IllegalArgumentException If the provided message url is not valid
	 */
	@NotNull
	public static RestAction<Message> getMessageByJumpUrl(@NotNull String url, @NotNull JDA jda) throws IllegalArgumentException {
		Checks.notNull(url, "url");
		Checks.notNull(jda, "jda");

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
	 * @return {@link #label(String, int)} with max set to 25
	 */
	@NotNull
	public static String label(@NotNull String str) {
		return label(str, 25);
	}

	/**
	 * @param str The input string
	 * @param max The maximum number of characters
	 * @return The input string with the length being a maximum of max characters. If the input string was longer, the result will end with "..."
	 */
	@NotNull
	public static String label(@NotNull String str, int max) {
		Checks.notNull(str, "str");

		return str.length() > max
				? str.substring(0, max - 3) + "..."
				: str;
	}

	/**
	 * Combines multiple {@link RestAction}s of the same type together. In contrast to {@link RestAction#allOf(Collection)} this method can handle an empty input list. In that case a {@link CompletedRestAction} is returned
	 *
	 * @param jda     The {@link JDA} instance
	 * @param actions A list of {@link RestAction}s actions
	 * @param <T>     The {@link RestAction}s' type
	 * @return A combined {@link RestAction} of all provided actions.
	 */
	@NotNull
	public static <T> RestAction<List<T>> accumulate(@NotNull JDA jda, @NotNull Collection<? extends RestAction<? extends T>> actions) {
		Checks.notNull(jda, "jda");
		Checks.notNull(actions, "actions");

		return actions.isEmpty()
				? new CompletedRestAction<>(jda, Collections.emptyList())
				: RestAction.allOf(actions);
	}
}
