package g65058.dev3.labyrinthe.model.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command history manager (Invoker in Command pattern).
 * Manages the undo/redo stacks.
 */
public class CommandHistory {
    private final Deque<Command> undoStack;
    private final Deque<Command> redoStack;

    /**
     * Creates a new command history.
     */
    public CommandHistory() {
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    /**
     * Executes a command and adds it to the history.
     *
     * @param command the command to execute
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo stack when new command is executed
    }

    /**
     * Undoes the last command.
     *
     * @return true if a command was undone
     */
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }

    /**
     * Redoes the last undone command.
     *
     * @return true if a command was redone
     */
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        return true;
    }

    /**
     * @return true if there are commands to undo
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * @return true if there are commands to redo
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Clears the history.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * @return the number of commands in the undo stack
     */
    public int getUndoCount() {
        return undoStack.size();
    }

    /**
     * @return the number of commands in the redo stack
     */
    public int getRedoCount() {
        return redoStack.size();
    }
}
