package commands;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.option.Choice;
import de.mineking.discordutils.commands.option.Option;
import net.dv8tion.jda.api.interactions.commands.Command;
import shared.CommandContext;

import java.util.Arrays;
import java.util.List;

@ApplicationCommand(name = "choice")
public class ChoiceCommand {
	@Choice("test")
	public List<Command.Choice> choices = Arrays.asList(
			new Command.Choice("a", "a"),
			new Command.Choice("b", "b"),
			new Command.Choice("c", "c")
	);

	@ApplicationCommandMethod
	public void performCommand(CommandContext context, @Option(name = "test") String arg) {
		context.getEvent().reply(arg).queue();
	}
}
