package g65058.dev3.labyrinthe.model.command;

/**
 * Composite command representing a complete turn (insert + move).
 * Allows undoing/redoing a complete turn as a single operation.
 */
public class TurnCommand implements Command {
    private final InsertTileCommand insertCommand;
    private final MovePlayerCommand moveCommand;

    /**
     * Creates a new turn command.
     *
     * @param insertCommand the insert tile command
     * @param moveCommand   the move player command
     */
    public TurnCommand(InsertTileCommand insertCommand, MovePlayerCommand moveCommand) {
        this.insertCommand = insertCommand;
        this.moveCommand = moveCommand;
    }

    @Override
    public void execute() {
        insertCommand.execute();
        moveCommand.execute();
    }

    @Override
    public void undo() {
        // Undo in reverse order
        moveCommand.undo();
        insertCommand.undo();
    }
}
