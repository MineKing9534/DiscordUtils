package de.mineking.discord.commands.localization;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.interaction.Command;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.interaction.option.Option;
import de.mineking.discord.commands.localization.LocalizationMapper.LocalizationResult;

public class LocalizationUtils {
	private static Map<DiscordLocale, LocalizationResult> map(LocalizationMapper mapper, Function<DiscordLocale, LocalizationResult> handler) {
		/*LocalizationResult def = handler.apply(mapper.getDefaultLocale());
		
		return Stream.of(DiscordLocale.values())
				.filter(l -> !l.equals(DiscordLocale.UNKNOWN))
				.collect(
						Collectors.toMap(
								l -> l, 
								l -> mapper.getSupportedLocales().contains(l) ?
										handler::aply :
										def
						)
				);*/
		
		return mapper.getSupportedLocales().stream()
				.collect(
						Collectors.toMap(
								l -> l, 
								handler::apply
						)
				);
	}
	
	private static LocalizationHolder handle(DiscordLocale defaultLocale, Map<DiscordLocale, LocalizationResult> data) {
		return new LocalizationHolder(
				data.entrySet().stream()
				.filter(e -> e.getValue().name != null)
				.collect(
						Collectors.toMap(
								e -> e.getKey(), 
								e -> e.getValue().name
						)
				),	
				data.entrySet().stream()
				.filter(e -> e.getValue().description != null)
				.collect(
						Collectors.toMap(
								e -> e.getKey(), 
								e -> e.getValue().description
						)
				),
				defaultLocale
		);
	}
	
	public static LocalizationHolder handle(CommandManager cmdMan, BiFunction<LocalizationMapper, DiscordLocale, LocalizationResult> handler, Localizable obj) {
		LocalizationMapper mapper = cmdMan.getLocalizationMapper();
		
		if(mapper == null) {
			if(obj != null && obj.getLocalization() != null) {
				LocalizationInfo localization = obj.getLocalization();
				
				return LocalizationHolder.defaults(
						localization.name != null && localization.name.isConstant ? localization.name.key : null,
						localization.description != null && localization.description.isConstant ? localization.description.key : null
				);
			}
			
			return LocalizationHolder.empty();
		}
		
		return handle(
				mapper.getDefaultLocale(),
				map(
					mapper,
					l -> handler.apply(mapper, l)
				)
			);
	}
	
	public static LocalizationHolder handleCommand(Command<?, ?> cmd) {
		return handle(
				cmd.getFeature().getManager(),
				(mapper, l) -> mapper.mapCommand(l, cmd),
				cmd
		);
	}
	
	public static LocalizationHolder handleOption(SlashCommand cmd, Option o) {
		return handle(
				cmd.getFeature().getManager(),
				(mapper, l) -> mapper.mapOption(l, o, cmd),
				o
		);
	}
}
