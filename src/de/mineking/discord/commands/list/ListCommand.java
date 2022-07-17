package de.mineking.discord.commands.list;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.CommandPermission;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.interaction.context.SlashContext;
import de.mineking.discord.commands.interaction.option.Option;

public class ListCommand<T extends Listable> extends SlashCommand {
	public static boolean ephemeral = true;
	
	public static int defaultMax = 20;
	public static Option pageOption = new Option(OptionType.INTEGER, "page");
	
	private BiFunction<Guild, Member, T> getter;
	
	private BiPredicate<ListCommand<T>, SlashContext> cond;
	
	@SuppressWarnings("unused")
	private ListCommand() {}
	
	public ListCommand(@Nonnull CommandPermission perm, @Nonnull BiFunction<Guild, Member, T> getter) {
		this(perm, getter, true);
	}
	
	public ListCommand(@Nonnull CommandPermission perm, @Nonnull BiFunction<Guild, Member, T> getter, boolean page) {
		Checks.notNull(perm, "perm");
		Checks.notNull(getter, "getter");
		
		permission = perm;
		defaultAcknowledge = ephemeral;
		
		this.getter = getter;
		
		if(page) {
			addOption(pageOption);
		}
		
		cond = null;
	}
	
	public SlashCommand condition(BiPredicate<ListCommand<T>, SlashContext> cond) {
		this.cond = cond;
	
		return this;
	}
	
	
	
	@Override
	public void performCommand(SlashContext context) {
		if(cond != null) {
			if(!cond.test(this, context)) {
				return;
			}
		}
		
		Listable obj = getter.apply(context.guild, context.member);
		
		Map<String, Object> data = context.event.getOptions().stream()
				.collect(Collectors.toMap(OptionMapping::getName, OptionMapping::getAsString));
		
		int page = context.getOption("page", 1, OptionMapping::getAsInt);
		data.put("page", page);
		
		if(page != 1 && (page < 1 || page > obj.getMaxPages(data))) {
			context.cmdMan.getErrorHandler().invalidPage(context.data, page, obj.getMaxPages(data));
			
			return;	
		}

		context.event.getHook().editOriginal(
			obj.buildMessage(
					context.event.getUserLocale(), 
					context.guild, 
					context.member, 
					data
			)
		).queue(mes -> {
			context.cmdMan.addList(mes.getIdLong(), obj);
		});
	}
}
