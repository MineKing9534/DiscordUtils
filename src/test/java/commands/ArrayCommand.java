package commands;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.option.Option;
import de.mineking.discordutils.commands.option.OptionArray;

@ApplicationCommand(name = "array")
public class ArrayCommand {
	@ApplicationCommandMethod
	public void performCommand(ExampleBot.CommandContext context, @OptionArray(minCount = 2, maxCount = 5) @Option(name = "param") String... args) {
		context.event.reply(String.join(", ", args)).queue();
	}
}
