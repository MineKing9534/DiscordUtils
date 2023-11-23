package commands;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import shared.CommandContext;

@ApplicationCommand(name = "subcommand")
public class SubcommandTest {
	@ApplicationCommand(name = "a")
	public static class A {
		@ApplicationCommand(name = "aa")
		public static class AA {
			@ApplicationCommand(name = "aaa")
			public static class AAA {
				@ApplicationCommandMethod
				public void performCommand(CommandContext context) {
					context.getEvent().reply("test aaa").queue();
				}
			}

			@ApplicationCommand(name = "aab")
			public static class AAB {
				@ApplicationCommandMethod
				public void performCommand(CommandContext context) {
					context.getEvent().reply("test").queue();
				}
			}
		}

		@ApplicationCommand(name = "ab")
		public static class AB {
			@ApplicationCommand(name = "aba")
			public static class ABA {
				@ApplicationCommandMethod
				public void performCommand(CommandContext context) {
					context.getEvent().reply("test").queue();
				}
			}

			@ApplicationCommand(name = "abb")
			public static class ABB {
				@ApplicationCommandMethod
				public void performCommand(CommandContext context) {
					context.getEvent().reply("test").queue();
				}
			}
		}
	}

	@ApplicationCommand(name = "b")
	public static class B {
		@ApplicationCommand(name = "ba")
		public static class BA {
			@ApplicationCommandMethod
			public void performCommand(CommandContext context) {
				context.getEvent().reply("test").queue();
			}
		}

		@ApplicationCommand(name = "bb")
		public static class BB {
			@ApplicationCommandMethod
			public void performCommand(CommandContext context) {
				context.getEvent().reply("test").queue();
			}
		}
	}

	@ApplicationCommand(name = "c")
	public static class C {
		@ApplicationCommandMethod
		public void performCommand(CommandContext context) {
			throw new RuntimeException("asdasd");
		}
	}
}
