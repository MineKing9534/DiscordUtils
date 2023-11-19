package commands;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.option.Autocomplete;
import de.mineking.discordutils.commands.option.Option;
import shared.AutocompleteContext;
import shared.CommandContext;

public class MethodCommandTest {
	@Autocomplete("text")
	public void autocompleteTextA(AutocompleteContext context) {
		context.event.replyChoice("a", "a").queue();
	}

	@ApplicationCommand(name = "methoda")
	public void a(CommandContext context, @Option(name = "text") String text) {
		context.event.reply(text).queue();
	}

	@Autocomplete("textb")
	public void autocompleteTextB(AutocompleteContext context) {
		context.event.replyChoice("b", "b").queue();
	}

	@ApplicationCommand(name = "methodb")
	public void b(CommandContext context, @Option(name = "text", id = "textb") String text) {
		context.event.reply(text).queue();
	}
}
