package g65058.dev3.labyrinthe.controller;

import g65058.dev3.labyrinthe.model.board.Arrow;
import g65058.dev3.labyrinthe.model.board.Position;
import g65058.dev3.labyrinthe.model.game.*;
import g65058.dev3.labyrinthe.model.observer.Observer;
import javafx.application.Platform;

/**
 * Controller for the Labyrinth game.
 * Handles user interactions and coordinates between view and model.
 */
public class GameController {
    private final LabyrinthFacade facade;

    /**
     * Creates a new controller.
     *
     * @param facade the game facade
     */
    public GameController(LabyrinthFacade facade) {
        this.facade = facade;
    }

    /**
     * Adds an observer to the model.
     *
     * @param observer the observer
     */
    public void addObserver(Observer observer) {
        facade.addObserver(observer);
    }

    /**
     * Starts a new game.
     *
     * @param humanCount        number of human players
     * @param aiDifficulty      AI difficulty level
     * @param simplifiedVersion use simplified win condition
     */
    public void startNewGame(int humanCount, int aiDifficulty, boolean simplifiedVersion) {
        facade.startNewGame(humanCount, aiDifficulty, simplifiedVersion);
    }

    /**
     * @return true if a game is running
     */
    public boolean isGameRunning() {
        return facade.isGameRunning();
    }

    /**
     * @return true if game is finished
     */
    public boolean isFinished() {
        return facade.isFinished();
    }

    /**
     * @return true if in insert phase
     */
    public boolean isInsertPhase() {
        return facade.getState() == GameState.WAITING_INSERT;
    }

    /**
     * @return true if in move phase
     */
    public boolean isMovePhase() {
        return facade.getState() == GameState.WAITING_MOVE;
    }

    /**
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return facade.getCurrentPlayer();
    }

    /**
     * @return the winner
     */
    public Player getWinner() {
        return facade.getWinner();
    }

    /**
     * Rotates the spare tile.
     */
    public void rotateSpareTile() {
        if (isInsertPhase()) {
            facade.rotateSpareTile();
        }
    }

    /**
     * Inserts tile at the specified arrow.
     *
     * @param arrow the insertion arrow
     */
    public void insertTile(Arrow arrow) {
        if (isInsertPhase() && facade.canInsert(arrow)) {
            facade.insertTile(arrow);

            // If current player is AI, play their move
            checkAndPlayAI();
        }
    }

    /**
     * Moves the current player to the destination.
     *
     * @param destination the target position
     */
    public void movePlayer(Position destination) {
        if (isMovePhase() && facade.canMove(destination)) {
            facade.movePlayer(destination);

            // Check if next player is AI
            checkAndPlayAI();
        }
    }

    /**
     * Checks if current player is AI and plays their turn.
     */
    private void checkAndPlayAI() {
        if (facade.isCurrentPlayerRobot() && isGameRunning()) {
            // Use Platform.runLater to allow UI to update
            Platform.runLater(() -> {
                try {
                    Thread.sleep(500); // Small delay for visual feedback
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                facade.playAITurn();

                // Continue if next player is also AI
                if (facade.isCurrentPlayerRobot() && isGameRunning()) {
                    checkAndPlayAI();
                }
            });
        }
    }

    /**
     * @return true if undo is available
     */
    public boolean canUndo() {
        return facade.canUndo();
    }

    /**
     * @return true if redo is available
     */
    public boolean canRedo() {
        return facade.canRedo();
    }

    /**
     * Undoes the last action.
     */
    public void undo() {
        if (canUndo()) {
            facade.undo();
        }
    }

    /**
     * Redoes the last undone action.
     */
    public void redo() {
        if (canRedo()) {
            facade.redo();
        }
    }

    /**
     * Abandons the current game.
     */
    public void abandon() {
        facade.abandon();
    }
}
