package g65058.dev3.labyrinthe.model.command;

import g65058.dev3.labyrinthe.model.board.*;
import g65058.dev3.labyrinthe.model.game.LabyrinthGame;
import g65058.dev3.labyrinthe.model.game.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Command for inserting a tile and shifting a row/column.
 * Stores all state needed for undo.
 */
public class InsertTileCommand implements Command {
    private final LabyrinthGame game;
    private final Arrow arrow;
    private final Direction spareTileOrientation;

    // State for undo
    private Tile[][] previousTiles;
    private Tile previousSpareTile;
    private Arrow previousLastArrow;
    private Map<Integer, Position> previousPlayerPositions;

    /**
     * Creates a new insert tile command.
     *
     * @param game   the game
     * @param arrow  the insertion arrow
     * @param orientation the orientation to set for the spare tile
     */
    public InsertTileCommand(LabyrinthGame game, Arrow arrow, Direction orientation) {
        this.game = game;
        this.arrow = arrow;
        this.spareTileOrientation = orientation;
    }

    @Override
    public void execute() {
        // Save state before execution
        Board board = game.getBoard();
        previousTiles = new Tile[Board.SIZE][Board.SIZE];
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                previousTiles[r][c] = board.getTile(r, c).copy();
            }
        }
        previousSpareTile = board.getSpareTile().copy();
        previousLastArrow = board.getLastArrow();

        // Save player positions
        previousPlayerPositions = new HashMap<>();
        for (Player player : game.getPlayers()) {
            previousPlayerPositions.put(player.getId(), player.getPosition());
        }

        // Execute the insertion
        game.performInsertion(arrow, spareTileOrientation);
    }

    @Override
    public void undo() {
        // Restore board state
        game.restoreBoardState(previousTiles, previousSpareTile, previousLastArrow);

        // Restore player positions
        for (Player player : game.getPlayers()) {
            Position savedPos = previousPlayerPositions.get(player.getId());
            if (savedPos != null) {
                player.setPosition(savedPos);
            }
        }
    }
}
