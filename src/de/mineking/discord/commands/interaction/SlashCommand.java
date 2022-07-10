package de.mineking.discord.commands.interaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.CommandPermission;
import de.mineking.discord.commands.history.ExecutionData;
import de.mineking.discord.commands.interaction.context.SlashContext;
import de.mineking.discord.commands.interaction.option.Option;
import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public abstract class SlashCommand extends Command<SlashCommandInteractionEvent, SlashContext> {
	private SlashCommand owner;
	
	private Map<String, SlashCommand> subcommands;
	
	private String description;
	
	private List<Function<Guild, Option>> options;
	
	public SlashCommand() {
		super(SlashCommandInteractionEvent.class);
		
		subcommands = new LinkedHashMap<>();
		
		options = new ArrayList<>();
		
		owner = null;
	}

	@Override
	protected final SlashContext buildContext(ExecutionData<SlashCommandInteractionEvent, SlashContext> data) {
		return new SlashContext(data);
	}
	
	@Override
	public Feature getFeature() {
		if(owner == null && feature == null) {
			throw new IllegalStateException("This comman wasn't added to a feature yet!");
		}
		
		else {
			return owner == null ? feature : owner.getFeature();
		}
	}
	
	/**
	 * @return The owning command or {@code null} if this command isn't registered as a subcommand
	 */
	@Nullable
	public SlashCommand getOwner() {
		return owner;
	}
	
	@Override
	public String getPath() {
		return owner == null ? super.getPath() : (owner.getPath() + "." + getName());
	}
	
	@Override
	public CommandPermission getPermission() {
		return permission != null ? permission : (
					owner == null ?
					super.getPermission() :
					owner.getPermission()
			);
	}
	
	/**
	 * Sets the default description for this command
	 * 
	 * @param str
	 * 		The new default description
	 * 
	 * @return The same SlashCommand instance
	 */
	@Nonnull
	public final SlashCommand setDescription(@Nullable String str) {
		this.description = str;
		
		return this;
	}
	
	/**
	 * @return The description of this command
	 */
	@Nonnull
	public final String getDescription() {
		return description == null ? getPath() : description;
	}
	
	private SlashCommand buildSubcommand(String name, SlashCommand cmd) {
		cmd.feature = feature;
		cmd.owner = this;
		cmd.name = name;
		
		if(cmd.permission == null) {
			cmd.permission = permission;
		}
		
		return cmd;
	}
	
	/**
	 * @return All subcommands added to this SlashCommand including a help command if the CommandManager has one registered
	 */
	@Nonnull
	public final Map<String, SlashCommand> getSubcommands() {
		Map<String, SlashCommand> temp = new LinkedHashMap<>(subcommands);
		
		if(!temp.isEmpty() && feature.getManager().getHelpCommand() != null && !subcommands.containsKey("help") && owner == null) {
			temp.put("help", buildSubcommand("help", feature.getManager().getHelpCommand()));
		}
		
		return temp;
	}
	
	/**
	 * @return The raw subcommands. This only includes commands that were actually added to this SlashCommand
	 */
	@Nonnull
	public final Map<String, SlashCommand> getSubcommandsRaw() {
		return subcommands;
	}
	
	/**
	 * Adds a subcommand to this SlashCommand
	 * 
	 * @param name
	 * 		The name of the subcommand
	 * 
	 * @param cmd
	 * 		A SlashCommand
	 * 
	 * @return The same SlashCommand instance
	 */
	@Nonnull
	public final SlashCommand addSubcommand(@Nonnull String name, @Nonnull SlashCommand cmd) {
		Checks.nonNull(name, "name");
		Checks.nonNull(cmd, "cmd");
		
		if(subcommands.containsKey(name)) {
			throw new IllegalArgumentException("There already is a subcommands registered with the name '" + name + "'");
		}
		
		subcommands.put(name, buildSubcommand(name, cmd));
		
		return this;
	}
	
	/**
	 * Adds a option to to this command. These options will be used in the build method
	 * 
	 * @param handler
	 * 		A handler, how to get the Option for a guild
	 * 
	 * @return The same SlashCommand instance
	 */
	@Nonnull
	public final SlashCommand addOption(@Nonnull Function<Guild, Option> handler) {
		Checks.nonNull(handler, "handler");
		
		this.options.add(handler::apply);
		
		return this;
	}
	
	/**
	 * Adds a option to to this command. These options will be used in the build method
	 * 
	 * @param option
	 * 		A new Option for this command
	 * 
	 * @return The same SlashCommand instance
	 */
	@Nonnull
	public final SlashCommand addOption(@Nonnull Option option){
		Checks.nonNull(option, "option");
		
		this.options.add(g -> option);
		
		return this;
	}
	
	private List<SubcommandData> buildSubcommands(Guild g) {
		return getSubcommands().values().stream()
				.filter(cmd -> cmd.subcommands.isEmpty())
				.map(cmd -> {
					SubcommandData data = new SubcommandData(cmd.getName(), cmd.getDescription())
							.addOptions(cmd.buildOptions(g));
				
					if(cmd.getFeature().getManager().getLocalizationMapper() != null) {
						Map<DiscordLocale, String> locales = cmd.getFeature().getManager().getLocalizationMapper().apply(cmd.getPath());
						
						data.setDescriptionLocalizations(locales);
						
						if(cmd.getFeature().getManager().getDefaultLanguage() != null) {
							data.setDescription(locales.get(cmd.getFeature().getManager().getDefaultLanguage()));
						}
					}
					
					return data;
				})
				.toList();
	}
	
	private List<SubcommandGroupData> buildGroups(Guild g) {
		return getSubcommands().values().stream()
				.filter(cmd -> !cmd.subcommands.isEmpty())
				.map(cmd -> {
					SubcommandGroupData data = new SubcommandGroupData(cmd.getName(), cmd.getDescription())
						.addSubcommands(cmd.buildSubcommands(g));
					
					if(cmd.getFeature().getManager().getLocalizationMapper() != null) {
						Map<DiscordLocale, String> locales = cmd.getFeature().getManager().getLocalizationMapper().apply(cmd.getPath());
						
						data.setDescriptionLocalizations(locales);
						
						if(cmd.getFeature().getManager().getDefaultLanguage() != null) {
							data.setDescription(locales.get(cmd.getFeature().getManager().getDefaultLanguage()));
						}
					}
					
					return data;
				})
				.toList();
	}

	@Override
	@Nonnull
	public CommandData build(@Nonnull Guild g) {
		Checks.nonNull(g, "g");
		
		if(getFeature() == null) {
			throw new IllegalStateException("You can only build commands attached to a feature");
		}
		
		SlashCommandData data = Commands.slash(getName(), getDescription());
		
		if(getFeature().getManager().getLocalizationMapper() != null) {
			Map<DiscordLocale, String> locales = getFeature().getManager().getLocalizationMapper().apply(getPath());
			
			data.setDescriptionLocalizations(locales);
			
			if(getFeature().getManager().getDefaultLanguage() != null) {
				data.setDescription(locales.get(getFeature().getManager().getDefaultLanguage()));
			}
		}
		
		if(options.isEmpty()) {
			data.addSubcommandGroups(buildGroups(g));
			data.addSubcommands(buildSubcommands(g));
		}
		
		else {
			data.addOptions(buildOptions(g));
		}
		
		return data;
	}
	
	private final List<OptionData> buildOptions(Guild g) {
		return getOptions(g).stream()
				.map(o -> o.build(this))
				.toList();
	}
	
	/**
	 * @param g
	 * 		The guild you want to get the options for
	 * 
	 * @return All options for the specified guild
	 */
	@Nonnull
	public final List<Option> getOptions(@Nonnull Guild g) {
		Checks.nonNull(g, "g");
		
		return options.stream()
				.map(f -> f.apply(g))
				.toList();
	}
	
	static void updateSubcommands(SlashCommand cmd) {
		for(SlashCommand c : cmd.subcommands.values()) {
			c.owner = cmd;
			
			updateSubcommands(c);
		}
	}
}
