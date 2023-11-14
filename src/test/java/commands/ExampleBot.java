package commands;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.commands.context.ContextBase;
import de.mineking.discordutils.console.RedirectTarget;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

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

		discordUtils = new DiscordUtils<>(jda, this)
				.mirrorConsole(RedirectTarget.directMessage(UserSnowflake.fromId(723571803133313055L)))
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
				);
	}

	public static class CommandContext extends ContextBase<GenericCommandInteractionEvent> {
		public CommandContext(@NotNull GenericCommandInteractionEvent event) {
			super(event);
		}
	}

	public static class AutocompleteContext extends ContextBase<CommandAutoCompleteInteractionEvent> {
		public AutocompleteContext(@NotNull CommandAutoCompleteInteractionEvent event) {
			super(event);
		}
	}
}
