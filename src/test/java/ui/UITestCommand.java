package ui;

import de.mineking.discordutils.DiscordUtils;
import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.MessageRenderer;
import de.mineking.discordutils.ui.components.button.ButtonColor;
import de.mineking.discordutils.ui.components.button.ButtonComponent;
import de.mineking.discordutils.ui.components.button.ToggleComponent;
import de.mineking.discordutils.ui.components.types.ComponentRow;
import net.dv8tion.jda.api.EmbedBuilder;
import shared.CommandContext;

@ApplicationCommand(name = "test")
public class UITestCommand {
	public final MessageMenu menu;

	public UITestCommand(DiscordUtils<?> manager) {
		menu = manager.getUIManager().createMenu(
				"test",
				MessageRenderer.embed(state -> new EmbedBuilder()
						.setTitle("Test Menu")
						.addField("Text", state.getState("text", String.class), false)
						.addField("Last user", state.event().map(e -> e.getUser().toString()).orElse("*none*"), false)
						.build()
				),
				ComponentRow.of(
						new ButtonComponent("button", ButtonColor.BLUE, "Append !")
								.appendHandler(state -> {
									state.setState("text", String.class, current -> current + "!");
									state.update();
								}),
						new ToggleComponent("toggle", state -> state ? ButtonColor.GREEN : ButtonColor.RED, "Toggle")
				)
		).<Boolean>effect("toggle", (state, name, old, value) -> System.out.println("Toggle value changed: " + value));
	}

	@ApplicationCommandMethod
	public void performCommand(CommandContext context) {
		menu.createState()
				.setState("text", "abc")
				.setState("toggle", true)
				.display(context.getEvent(), false);

	}
}
