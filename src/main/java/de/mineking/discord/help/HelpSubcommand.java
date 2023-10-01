package de.mineking.discord.help;

import de.mineking.discord.commands.ContextBase;
import de.mineking.discord.commands.annotated.ApplicationCommand;
import de.mineking.discord.commands.annotated.ApplicationCommandMethod;
import de.mineking.discord.localization.LocalizationPath;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@LocalizationPath("subhelp")
@ApplicationCommand(name = "help", defer = true)
public class HelpSubcommand {
	private final HelpTarget command;

	public HelpSubcommand(HelpTarget command) {
		this.command = command;
	}

	@ApplicationCommandMethod
	public void performCommand(ContextBase context, SlashCommandInteractionEvent event) {
		context.manager.getManager().getHelpManager().displayHelp(event, command, false);
	}
}
