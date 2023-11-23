package commands;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.option.Option;
import de.mineking.discordutils.commands.option.OptionArray;
import shared.CommandContext;

@ApplicationCommand(name = "array")
public class ArrayCommand {
	@ApplicationCommandMethod
	public void performCommand(CommandContext context, @OptionArray(minCount = 2, maxCount = 5) @Option(name = "param") String... args) {
		context.getEvent().reply(String.join(", ", args)).queue();
	}
}
