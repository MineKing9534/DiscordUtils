import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import javax.security.auth.login.LoginException;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.CommandManagerBuilder;
import de.mineking.discord.commands.ConsoleCommand;
import de.mineking.discord.commands.interaction.Command;
import de.mineking.discord.commands.interaction.ContextCommand;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.interaction.UserCommand;
import de.mineking.discord.commands.interaction.context.AutocompleteContext;
import de.mineking.discord.commands.interaction.context.SlashContext;
import de.mineking.discord.commands.interaction.context.UserContext;
import de.mineking.discord.commands.interaction.option.AutocompleteOption;
import de.mineking.discord.commands.interaction.option.Choice;
import de.mineking.discord.commands.interaction.option.Option;
import de.mineking.discord.commands.localization.LocalizationInfo;
import de.mineking.discord.commands.localization.LocalizationInfo.LocalizationPackage;
import de.mineking.discord.commands.localization.LocalizationMapper;

public class Main {
	public static final String TOKEN = "-- your bot token --";
	public static JDA jda;
	
	
	public static void main(String[] args) {
		CommandManager cmdMan = CommandManagerBuilder.createDefault()
				.registerConsoleCommand("exit", new ConsoleCommand() {
					@Override
					public void performCommand(String command, String[] args) {
						jda.shutdown();
					}
				})
				.registerCommand("help", new HelpCommand())
				//You can use setFeatureStateGetter on CommandManagerBuilder to choose whether a feature should be enabled or disabled for a specific guild. You can use this to make users be able to select which features should be active on their server.
				.createFeature("moderation", feature -> {
					feature.addCommand("report", new ReportCommand());
					//add other commands like ban etc.
				})
				.build();
		
		//You can add a localization mapper to the CommandManagerBuilder to use localization instead of constant names and descriptions.
		//We don't add it here, to keep the example simple...
		final List<DiscordLocale> supportedLocales = Arrays.asList(DiscordLocale.ENGLISH_US, DiscordLocale.ENGLISH_UK, DiscordLocale.GERMAN);
		
		//Return the keys as localization result. Instead, you can read this from a file...
		final BiFunction<DiscordLocale, String, String> localizationFunction = (locale, key) -> key;
		
		LocalizationMapper localizationMapper = new LocalizationMapper() {
			@Override
			public List<DiscordLocale> getSupportedLocales() {
				//The mapX methods will be called for these locales
				return supportedLocales;
			}
			
			@Override
			public DiscordLocale getDefaultLocale() {
				//This is used to set default values for unsupported languages
				return DiscordLocale.ENGLISH_US;
			}
			
			//very simple localiization functions
			@Override
			public LocalizationResult mapCommand(DiscordLocale locale, Command<?, ?> cmd) {
				String description = localizationFunction.apply(locale, cmd.getPath());
				
				return cmd instanceof ContextCommand ?
						LocalizationResult.name(description) :
						LocalizationResult.description(description);
			}
			
			@Override
			public LocalizationResult mapOption(DiscordLocale locale, Option o, SlashCommand cmd) {
				return o.getLocalization().handleDescription(cmd.getPath() + "." + o.getName(), k -> localizationFunction.apply(locale, k));
			}
		};
		
		try {
			jda = JDABuilder.createDefault(TOKEN)
					.addEventListeners(cmdMan)
					.addEventListeners(new ListenerAdapter() {
						@Override
						public void onGuildReady(GuildReadyEvent event) {
							//Update commands on startup
							cmdMan.updateCommands(event.getGuild()).queue();
						}
					})
					.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	public static class HelpCommand extends SlashCommand {
		private final static List<String> helpTargets = Arrays.asList(
				
		);
		
		public HelpCommand() {
			//add an autocomplete option
			addOption(
					new AutocompleteOption(OptionType.STRING, "target", true) {
						@Override
						public List<Choice> handle(AutocompleteContext context) {
							return helpTargets.stream()
									.filter(t -> t.contains(context.current.getValue()))
									.map(t -> new Choice(t, t))
									.toList();
						}
					}
			);
			
			//Set a constant description
			localization = LocalizationInfo.description(LocalizationPackage.constant("Show the target in the console"));
		}
		
		@Override
		protected void performCommand(SlashContext context) {
			System.out.println(context.member + " requestet help for " + context.getOption("target").getAsString());
			
			//use context.event to reply. If you want to edit the defer reply, use context.event.getHook().editOriginal(...)
			//If you don't want the library to defer reply automatically, you can change its behavior with the defaultAcknowledge field from the constructor
		}
	}
	
	public static class ReportCommand extends UserCommand {
		@Override
		protected void performCommand(UserContext context) {
			System.out.println(context.target.getEffectiveName() + " was reported!");
			
			//use context.event to reply. If you want to edit the defer reply, use context.event.getHook().editOriginal(...)
			//If you don't want the library to defer reply automatically, you can change its behavior with the defaultAcknowledge field from the constructor
		}
		
	}
}
