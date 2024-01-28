package ui;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.console.RedirectTarget;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import shared.AutocompleteContext;
import shared.CommandContext;

public class ExampleBot {
	public static void main(String[] args) {
		new ExampleBot(args[0]);
	}

	public final JDA jda;
	public final DiscordUtils<ExampleBot> discordUtils;

	public ExampleBot(String token) {
		var builder = JDABuilder.createDefault(token)
				.setStatus(OnlineStatus.ONLINE);

		this.discordUtils = DiscordUtils.create(builder, this)
				.mirrorConsole(RedirectTarget.channel(1174751088575000666L))
				.useEventManager(null)
				.useUIManager(null)
				.useCommandManager(
						CommandContext::new,
						AutocompleteContext::new,
						config -> config
								.registerCommand(UITestCommand.class)
								.updateCommands()
				).build();

		this.jda = discordUtils.getJDA();
	}
}
