package de.mineking.discord.commands.list;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import de.mineking.discord.commands.CommandPermission;
import de.mineking.discord.commands.history.RuntimeData;
import de.mineking.discord.commands.interaction.SlashCommand;
import de.mineking.discord.commands.interaction.option.Option;
import de.mineking.exceptions.Checks;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class ListCommand<T extends Listable> extends SlashCommand {
	public static boolean ephemeral = true;
	
	public static int defaultMax = 20;
	public static Option pageOption = new Option(OptionType.INTEGER, "page");
	
	private BiFunction<Guild, Member, T> getter;
	
	private BiPredicate<ListCommand<T>, Map<String, OptionMapping>> cond;
	
	@SuppressWarnings("unused")
	private ListCommand() {}
	
	public ListCommand(@Nonnull CommandPermission perm, @Nonnull BiFunction<Guild, Member, T> getter) {
		this(perm, getter, true);
	}
	
	public ListCommand(@Nonnull CommandPermission perm, @Nonnull BiFunction<Guild, Member, T> getter, boolean page) {
		Checks.nonNull(perm, "perm");
		Checks.nonNull(getter, "getter");
		
		permission = perm;
		defaultAcknowledge = ephemeral;
		
		this.getter = getter;
		
		if(page) {
			addOption(pageOption);
		}
		
		cond = null;
	}
	
	public SlashCommand condition(BiPredicate<ListCommand<T>, Map<String, OptionMapping>> cond) {
		this.cond = cond;
	
		return this;
	}
	
	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Map<String, OptionMapping> args) {
		if(cond != null) {
			if(!cond.test(this, args)) {
				return;
			}
		}
		
		Listable obj = getter.apply(channel.getGuild(), m);
		
		Map<String, Object> data = args.entrySet().stream()
				.collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue().getAsString()));
		
		int page = args.get("page") == null ? 1 : args.get("page").getAsInt();
		data.put("page", page);
		
		if(page != 1 && (page < 1 || page > obj.getMaxPages(data))) {
			getFeature().getManager().getErrorHandler().invalidPage(getData(), page, obj.getMaxPages(data));
			
			return;	
		}

		reply(
			obj.buildMessage(
					getEvent().getUserLocale(), 
					channel.getGuild(), 
					m, 
					data
			)
		).queue((mes) -> {
			getFeature().getManager().addList(mes.getIdLong(), obj);
		});
	}
	
	public RuntimeData getData() {
		return getRuntimeData();
	}
}
