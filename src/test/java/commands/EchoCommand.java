package commands;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.option.Autocomplete;
import de.mineking.discordutils.commands.option.Option;
import shared.AutocompleteContext;
import shared.CommandContext;

import java.util.Optional;

@ApplicationCommand(name = "echo")
public class EchoCommand {
	@Autocomplete("text")
	public void autocomplete(AutocompleteContext context) {
		context.getEvent().replyChoice(context.getEvent().getFocusedOption().getValue() + "!", context.getEvent().getFocusedOption().getValue() + "!").queue();
	}

	@ApplicationCommandMethod
	public void performCommand(CommandContext context, @Option(name = "text", required = false) Optional<String> text) {
		text.ifPresentOrElse(
				x -> context.getEvent().reply(x).queue(),
				() -> context.getEvent().reply("*nothing*").queue()
		);
	}
}
