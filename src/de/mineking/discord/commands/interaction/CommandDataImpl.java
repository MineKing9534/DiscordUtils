package de.mineking.discord.commands.interaction;

import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.CommandManager;
import de.mineking.discord.commands.interaction.option.Option;
import de.mineking.discord.commands.localization.LocalizationMapper;
import de.mineking.discord.commands.localization.LocalizationMapper.LocalizationResult;

public class CommandDataImpl extends net.dv8tion.jda.internal.interactions.CommandDataImpl {
	private Command<?, ?> cmd;
	
	public CommandDataImpl(@Nonnull String name, @Nonnull String description, @Nonnull SlashCommand cmd) {
		super(name, description);
		
		Checks.notNull(cmd, "cmd");
		this.cmd = cmd;
	}

	public CommandDataImpl(@Nonnull net.dv8tion.jda.api.interactions.commands.Command.Type type, @Nonnull String name, @Nonnull ContextCommand<?, ?> cmd) {
		super(type, name);
		
		Checks.notNull(cmd, "cmd");
		this.cmd = cmd;
	}
	
	private static Map<DiscordLocale, LocalizationResult> map(LocalizationMapper mapper, Function<DiscordLocale, LocalizationResult> handler) {
		if(mapper == null) {
			return new HashMap<>();
		}
		
		LocalizationResult def = handler.apply(mapper.getDefaultLocale());
		
		return Stream.of(DiscordLocale.values())
				.filter(l -> !l.equals(DiscordLocale.UNKNOWN))
				.collect(
						Collectors.toMap(
								l -> l, 
								l -> mapper.getSupportedLocales().contains(l) ?
										handler.apply(l) :
										def
						)
				);
	}
	
	public static class LocalizationHolder {
		public final Map<DiscordLocale, String> name;
		public final Map<DiscordLocale, String> description;
		
		public LocalizationHolder(Map<DiscordLocale, String> name,Map<DiscordLocale, String> description) {
			this.name = name;
			this.description = description;
		}
	}
	
	private static LocalizationHolder handle(Map<DiscordLocale, LocalizationResult> data) {
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
				)
		);
	}
	
	public static LocalizationHolder handle(CommandManager cmdMan, BiFunction<LocalizationMapper, DiscordLocale, LocalizationResult> handler) {
		LocalizationMapper mapper = cmdMan.getLocalizationMapper();
		
		return handle(
				map(
					mapper,
					l -> handler.apply(mapper, l)
				)
			);
	}
	
	public static LocalizationHolder handleCommand(Command<?, ?> cmd) {
		return handle(
				cmd.getFeature().getManager(),
				(mapper, l) -> mapper.mapCommand(l, cmd)
		);
	}
	
	public static LocalizationHolder handleOption(SlashCommand cmd, Option o) {
		return handle(
				cmd.getFeature().getManager(),
				(mapper, l) -> mapper.mapOption(l, o, cmd)
		);
	}
	
	@Override
	public DataObject toData() {
		LocalizationHolder holder = handleCommand(cmd);
		
		if(getType().equals(net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH)) {
			setDescriptionLocalizations(holder.description);
		}
		
		setNameLocalizations(holder.name);
		
		DataObject json = DataObject.empty()
                .put("type", getType().getId())
                .put("name", name)
                .put("options", options)
                .put("dm_permission", !isGuildOnly())
                .put("default_member_permissions", getDefaultPermissions() == DefaultMemberPermissions.ENABLED
                        ? null
                        : Long.toUnsignedString(getDefaultPermissions().getPermissionsRaw()))
                .put("name_localizations", getNameLocalizations())
                .put("options", options);
        if (getType() == net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH)
            json.put("description", description)
                .put("description_localizations", getDescriptionLocalizations());
        return json;
	}
}
