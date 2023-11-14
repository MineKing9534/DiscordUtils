package commands;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.option.Autocomplete;
import de.mineking.discordutils.commands.option.Option;

public class MethodCommandTest {
	@Autocomplete("text")
	public void autocompleteTextA(ExampleBot.AutocompleteContext context) {
		context.event.replyChoice("a", "a").queue();
	}

	@ApplicationCommand(name = "methoda")
	public void a(ExampleBot.CommandContext context, @Option(name = "text") String text) {
		context.event.reply(text).queue();
	}

	@Autocomplete("textb")
	public void autocompleteTextB(ExampleBot.AutocompleteContext context) {
		context.event.replyChoice("b", "b").queue();
	}

	@ApplicationCommand(name = "methodb")
	public void b(ExampleBot.CommandContext context, @Option(name = "text", id = "textb") String text) {
		context.event.reply(text).queue();
	}
}
