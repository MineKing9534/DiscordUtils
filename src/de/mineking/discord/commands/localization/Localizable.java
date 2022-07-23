package de.mineking.discord.commands.localization;

import javax.annotation.Nullable;

public interface Localizable {
	@Nullable
	public LocalizationInfo getLocalization();
}
