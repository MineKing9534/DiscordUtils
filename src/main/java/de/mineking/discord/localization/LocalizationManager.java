package de.mineking.discord.localization;

import de.mineking.discord.commands.Choice;
import de.mineking.discord.commands.CommandImplementation;
import de.mineking.discord.commands.annotated.option.Option;
import de.mineking.discord.commands.inherited.LocalizedChoice;
import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class LocalizationManager {
	public final BiFunction<LocalizationInfo, DiscordLocale, String> mapper;
	public final DiscordLocale defaultLocale;
	public final Collection<DiscordLocale> locales;

	public LocalizationManager(BiFunction<LocalizationInfo, DiscordLocale, String> mapper, DiscordLocale defaultLocale, Collection<DiscordLocale> locales) {
		this.mapper = mapper;
		this.defaultLocale = defaultLocale;
		this.locales = locales;
	}

	public static LocalizationManager createDefault() {
		return new LocalizationManager(
				(info, locale) -> info.path,
				DiscordLocale.ENGLISH_US,
				Collections.emptySet()
		);
	}

	public LocalizationPackage getCommandDescription(CommandImplementation command) {
		if(command.info.description.isEmpty()) {
			var custom = command.type.getAnnotation(LocalizationPath.class);

			if(custom == null) {
				return localize(new LocalizationInfo(command.getLocalizationPath()));
			} else {
				return localize(new LocalizationInfo(custom.value()));
			}
		} else {
			return LocalizationPackage.constant(command.info.description);
		}
	}

	public LocalizationPackage getOptionDescription(String command, String name, Option option, LocalizationPath custom) {
		if(option.description().isEmpty()) {
			if(custom == null) {
				return localize(new LocalizationInfo(command + "." + name));
			} else {
				return localize(new LocalizationInfo(custom.value()));
			}
		} else {
			return LocalizationPackage.constant(option.description());
		}
	}

	public LocalizationPackage getOptionDescription(String command, de.mineking.discord.commands.inherited.Option option) {
		if(option.description.isEmpty()) {
			return localize(new LocalizationInfo(command + "." + option.name));
		} else if(option.localize) {
			return localize(new LocalizationInfo(option.description));
		} else {
			return LocalizationPackage.constant(option.description);
		}
	}

	public LocalizationPackage getChoiceDescription(String command, String option, Choice choice) {
		if(choice instanceof LocalizedChoice lc) {
			if(!lc.custom) {
				return localize(new LocalizationInfo(command + "." + option + "." + choice.name));
			} else {
				return localize(new LocalizationInfo(choice.name));
			}
		} else {
			return LocalizationPackage.constant(choice.name);
		}
	}

	public LocalizationPackage getMetaDataName(String key) {
		return localize(new LocalizationInfo("meta." + key + ".name"));
	}

	public LocalizationPackage getMetaDataDescription(String key) {
		return localize(new LocalizationInfo("meta." + key + ".description"));
	}

	protected LocalizationPackage localize(LocalizationInfo info) {
		return new LocalizationPackage(
				mapper.apply(info, defaultLocale),
				locales.stream()
						.collect(Collectors.toMap(
								l -> l,
								l -> mapper.apply(info, l)
						))
		);
	}
}
