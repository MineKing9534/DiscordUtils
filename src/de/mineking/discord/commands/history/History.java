package de.mineking.discord.commands.history;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.mineking.discord.commands.CommandManager;
import de.mineking.exceptions.Checks;

public class History {
	private List<ExecutionData<?, ?>> history;
	private CommandManager cmdMan;	
	
	public History(CommandManager cmdMan) {
		history = new LinkedList<>();
		
		this.cmdMan = cmdMan;
	}
	
	/**
	 * Gets the ExecutionData at a specific position (0 is the last execution)
	 * 
	 * @param position
	 * 		The position
	 * 
	 * @return The HistoryEntry at a specific position or the last if position is greater or equal to getSize()
	 */
	public ExecutionData<?, ?> get(int position) {
		return (position < getSize() ? history.get(position) : getLast());
	}
	
	/**
	 * @return The last HistoryEntry or null if this history is empty
	 */
	@Nullable
	public ExecutionData<?, ?> getLast() {
		return history.size() == 0 ? null : history.get(0);
	}
	
	/**
	 * @return wether this history has a last element
	 */
	public boolean hasLast() {
		return getLast() != null;
	}
	
	/**
	 * Removes an entry from the history
	 * 
	 * @param position
	 * 		The position to remove
	 * 
	 * @return Whether the given position was valid
	 */
	public boolean remove(int position) {
		if(position < 0 || position >= history.size()) {
			return false;
		}
		
		history.remove(position);
		
		return true;
	}
	
	/**
	 * Adds a new HistoryEntry to this history
	 * 
	 * @param entry
	 * 		The HistoryEntry
	 */
	public void add(@Nonnull ExecutionData<?, ?> entry) {
		Checks.nonNull(entry, "entry");
		
		history.add(0, entry);
		
		if(cmdMan.getMaxHistoryLength() != null && history.size() > cmdMan.getMaxHistoryLength()) {
			history.remove(history.size() - 1);
		}
	}
	
	/**
	 * @return The current size of this history
	 */
	public int getSize() {
		return history.size();
	}
	
	/**
	 * @return A stream of all elements in the list
	 */
	public Stream<ExecutionData<?, ?>> stream() {
		return history.stream();
	}
}
