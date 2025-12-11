package g65058.dev3.labyrinthe.model.game;

/**
 * Represents the different states of the game.
 */
public enum GameState {
    /**
     * Game has not started yet
     */
    NOT_STARTED,

    /**
     * Waiting for tile insertion phase
     */
    WAITING_INSERT,

    /**
     * Waiting for player movement phase
     */
    WAITING_MOVE,

    /**
     * Game has finished with a winner
     */
    FINISHED,

    /**
     * Game was abandoned
     */
    ABORTED
}
