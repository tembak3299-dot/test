package g65058.dev3.labyrinthe.model.ai;

import g65058.dev3.labyrinthe.model.board.Board;
import g65058.dev3.labyrinthe.model.board.Tile;
import g65058.dev3.labyrinthe.model.game.Move;
import g65058.dev3.labyrinthe.model.game.Player;

/**
 * Strategy interface for AI players (Strategy pattern).
 */
public interface Strategy {
    /**
     * Chooses a move for the given player.
     *
     * @param board     the current board state
     * @param player    the player making the move
     * @param spareTile the spare tile to insert
     * @return the chosen move
     */
    Move chooseMove(Board board, Player player, Tile spareTile);

    /**
     * @return the name/description of this strategy
     */
    String getName();
}
