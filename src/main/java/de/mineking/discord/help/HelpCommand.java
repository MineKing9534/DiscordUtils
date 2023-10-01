package de.mineking.discord.help;

import de.mineking.discord.commands.ContextBase;
import de.mineking.discord.commands.annotated.ApplicationCommand;
import de.mineking.discord.commands.annotated.ApplicationCommandMethod;
import de.mineking.discord.commands.annotated.option.Option;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@ApplicationCommand(name = "help")
public class HelpCommand {
	public void handleAutocomplete(ContextBase context, CommandAutoCompleteInteractionEvent event) {
		event.replyChoices(
				context.manager.getManager().getHelpManager()
						.findTargets(event.getFocusedOption().getValue(), event)
						.limit(OptionData.MAX_CHOICES)
						.map(t -> new Command.Choice(t.getDisplay(event.getUserLocale()), t.getKey()))
						.toList()
		).queue();
	}

	@ApplicationCommandMethod
	public void performCommand(ContextBase context, SlashCommandInteractionEvent event, @Option(autocomplete = "handleAutocomplete", required = false) String target) {
		var help = context.manager.getManager().getHelpManager();

		help.displayHelp(
				event,
				help.getTarget(target).orElse(help.getMainTarget()),
				false
		);
	}
}
