package commands;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.console.RedirectTarget;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import shared.AutocompleteContext;
import shared.CommandContext;

import java.util.Optional;

public class ExampleBot {
	public static void main(String[] args) {
		new ExampleBot(args[0]);
	}

	public final JDA jda;
	public final DiscordUtils<ExampleBot> discordUtils;

	public ExampleBot(String token) {
		jda = JDABuilder.createDefault(token)
				.setStatus(OnlineStatus.ONLINE)
				.build();

		var methodTestInstance = Optional.of(new MethodCommandTest());

		discordUtils = DiscordUtils.create(jda, this)
				.mirrorConsole(RedirectTarget.directMessage(723571803133313055L))
				.useCommandManager(
						CommandContext::new,
						AutocompleteContext::new,
						config -> config
								.registerCommand(EchoCommand.class)
								.registerCommand(ChoiceCommand.class)
								.registerCommand(ArrayCommand.class)
								.registerCommand(EnumCommand.class)
								.registerCommand(SubcommandTest.class)
								.registerCommand(MethodCommandTest.class, c -> methodTestInstance, c -> methodTestInstance)
								.updateCommands()
				).build();
	}

}
