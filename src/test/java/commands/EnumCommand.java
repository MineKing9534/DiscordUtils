package commands;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.option.Option;
import shared.CommandContext;

@ApplicationCommand(name = "enum")
public class EnumCommand {
	public enum Test {
		A,
		B,
		C
	}

	@ApplicationCommandMethod
	public void performCommand(CommandContext context, @Option Test test) {
		context.event.reply(test.toString()).queue();
	}
}
