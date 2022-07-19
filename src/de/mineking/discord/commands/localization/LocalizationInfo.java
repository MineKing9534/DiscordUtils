package de.mineking.discord.commands.localization;

import net.dv8tion.jda.internal.utils.Checks;

import java.util.function.Function;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.localization.LocalizationMapper.LocalizationResult;

public class LocalizationInfo {
	public static class LocalizationPackage {
		public final String key;
		public final boolean isConstant;
		
		public LocalizationPackage(String key, boolean isConstant) {
			this.key = key;
			this.isConstant = isConstant;
		}
	}
	
	public final LocalizationPackage name;
	public final LocalizationPackage description;
	
	private LocalizationInfo(LocalizationPackage name, LocalizationPackage description) {
		this.name = name;
		this.description = description;
	}
	
	@Nonnull
	public static LocalizationInfo name(@Nonnull LocalizationPackage name) {
		Checks.notNull(name, "name");
		
		return new LocalizationInfo(name, null);
	}
	
	@Nonnull
	public static LocalizationInfo name(@Nonnull String name) {
		return name(new LocalizationPackage(name, false));
	}

	@Nonnull
	public static LocalizationInfo description(@Nonnull LocalizationPackage description) {
		Checks.notNull(description, "description");
		
		return new LocalizationInfo(null, description);
	}
	
	@Nonnull
	public static LocalizationInfo description(@Nonnull String description) {
		return description(new LocalizationPackage(description, false));
	}
	
	@Nonnull
	public static LocalizationInfo create(@Nonnull LocalizationPackage name, @Nonnull LocalizationPackage description) {
		Checks.notNull(name, "name");
		Checks.notNull(description, "description");
		
		return new LocalizationInfo(name, description);
	}
	
	@Nonnull
	public static LocalizationInfo createEmpty() {
		return new LocalizationInfo(null, null);
	}
	
	public LocalizationResult handleDescription(String path, Function<String, String> getter) {
		return LocalizationResult.description(
				description == null ?
				getter.apply(path) : (
					description.isConstant ? 
					description.key :
					getter.apply(description.key)
				)
			);
	}
	
	public LocalizationResult handleName(String path, Function<String, String> getter) {
		return LocalizationResult.name(
				name == null ?
				getter.apply(path) : (
					name.isConstant ? 
					name.key :
					getter.apply(name.key)
				)
			);
	}
}
