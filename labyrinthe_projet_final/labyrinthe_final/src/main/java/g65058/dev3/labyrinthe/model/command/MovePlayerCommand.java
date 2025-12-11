package g65058.dev3.labyrinthe.model.command;

import g65058.dev3.labyrinthe.model.board.Position;
import g65058.dev3.labyrinthe.model.game.LabyrinthGame;
import g65058.dev3.labyrinthe.model.game.Player;
import g65058.dev3.labyrinthe.model.game.ObjectiveCard;

/**
 * Command for moving a player to a new position.
 * Stores state needed for undo including objective achievement.
 */
public class MovePlayerCommand implements Command {
    private final LabyrinthGame game;
    private final Position destination;

    // State for undo
    private Position previousPosition;
    private int previousPlayerIndex;
    private ObjectiveCard achievedObjective;
    private boolean wasObjectiveAchieved;

    /**
     * Creates a new move player command.
     *
     * @param game        the game
     * @param destination the destination position
     */
    public MovePlayerCommand(LabyrinthGame game, Position destination) {
        this.game = game;
        this.destination = destination;
    }

    @Override
    public void execute() {
        Player player = game.getCurrentPlayer();
        previousPosition = player.getPosition();
        previousPlayerIndex = game.getCurrentPlayerIndex();

        // Check if this move achieves an objective
        wasObjectiveAchieved = game.wouldAchieveObjective(destination);
        if (wasObjectiveAchieved) {
            achievedObjective = player.getCurrentObjective();
        }

        // Execute the move
        game.performMove(destination);
    }

    @Override
    public void undo() {
        // Restore previous player as current
        game.setCurrentPlayerIndex(previousPlayerIndex);

        Player player = game.getCurrentPlayer();

        // Restore position
        player.setPosition(previousPosition);

        // Restore objective if it was achieved
        if (wasObjectiveAchieved && achievedObjective != null) {
            game.restoreObjective(player, achievedObjective);
        }

        // Reset game state to WAITING_MOVE
        game.setStateToWaitingMove();
    }
}
