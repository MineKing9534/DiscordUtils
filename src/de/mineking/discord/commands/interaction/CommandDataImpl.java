package de.mineking.discord.commands.interaction;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.localization.LocalizationHolder;
import de.mineking.discord.commands.localization.LocalizationUtils;

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
	
	@Override
	public DataObject toData() {
		LocalizationHolder holder = LocalizationUtils.handleCommand(cmd);
		
		if(getType().equals(net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH)) {
			setDescriptionLocalizations(holder.description);
			
			if(holder.defaultDescription != null) {
				setDescription(holder.defaultDescription);
			}
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
