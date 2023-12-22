package list;

import de.mineking.discordutils.list.ListContext;
import de.mineking.discordutils.list.Listable;
import de.mineking.discordutils.list.StringEntry;
import de.mineking.discordutils.ui.MessageMenu;
import de.mineking.discordutils.ui.state.DataState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public class TestList implements Listable<StringEntry> {
	@NotNull
	@Override
	public List<StringEntry> getEntries(@NotNull DataState<MessageMenu> state, @NotNull ListContext<StringEntry> context) {
		return IntStream.range(0, 55).mapToObj(i -> new StringEntry(i + ": " + Integer.toBinaryString(i))).toList();
	}
}
