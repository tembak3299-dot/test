package g65058.dev3.labyrinthe.model.command;

/**
 * Command interface for the Command pattern (undo/redo functionality).
 */
public interface Command {
    /**
     * Executes the command.
     */
    void execute();

    /**
     * Undoes the command, restoring the previous state.
     */
    void undo();
}
