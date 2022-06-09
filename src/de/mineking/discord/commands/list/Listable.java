package de.mineking.discord.commands.list;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.gson.Gson;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public interface Listable {
	@Nonnull
	public default MessageEmbed list(@Nonnull Locale lang, @Nonnull Guild guild, @Nonnull Member m, @Nonnull Map<String, Object> args) {
		return list(lang, guild, m, (int)args.get("page"));
	}

	@Nonnull
	public MessageEmbed list(@Nonnull Locale lang, @Nonnull Guild guild, @Nonnull Member m, int page);
	
	public default boolean showButtons() {
		return true;
	}
	
	public default int getMax() {
		return ListCommand.defaultMax;
	}
	
	public default int getCurrent(Map<String, Object> data) {
		return getCurrent();
	}
	
	public int getCurrent();
	
	public default boolean isEmpty() {
		return getCurrent() == 0;
	}
	
	public default int getMaxPages(Map<String, Object> data) {
		return (getCurrent(data) - 1) / getMax() + 1;
	}
	
	@Nonnull
	public default Message buildMessage(@Nonnull Locale locale, @Nonnull Guild guild, @Nonnull Member m, @Nonnull Map<String, Object> data) {
		MessageBuilder mb = new MessageBuilder(new EmbedBuilder(list(locale, guild, m, data))
				.build());
		
		if(showButtons()) {
			int page = (int)data.get("page");
			
			Button next = Button.secondary("list:next", Emoji.fromUnicode("➡"));
			Button pageDisplay = Button.secondary(new Gson().toJson(data), "📖 " + data.get("page") + "/" + getMaxPages(data)).asDisabled();
			Button back = Button.secondary("list:back", Emoji.fromUnicode("⬅"));
			
			if(page * getMax() >= getCurrent(data)) {
				next = next.asDisabled();
			}
			
			if(page <= 1) {
				back = back.asDisabled();
			}
			
			mb.setActionRows(ActionRow.of(back, pageDisplay, next));
		}
		
		return mb.build();
	}
}
