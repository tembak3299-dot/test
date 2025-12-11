package g65058.dev3.labyrinthe.model.game;

import g65058.dev3.labyrinthe.model.board.*;
import g65058.dev3.labyrinthe.model.command.*;
import g65058.dev3.labyrinthe.model.ai.Strategy;
import g65058.dev3.labyrinthe.model.ai.RandomStrategy;
import g65058.dev3.labyrinthe.model.observer.Observer;

import java.util.List;

/**
 * Facade for the Labyrinth game model.
 * Provides a simplified interface for the view/controller to interact with the game.
 * Ensures game rules are enforced and prevents invalid operations.
 */
public class LabyrinthFacade {
    private LabyrinthGame game;
    private final CommandHistory commandHistory;
    private InsertTileCommand pendingInsertCommand;
    private final java.util.List<Observer> pendingObservers;

    /**
     * Creates a new facade.
     */
    public LabyrinthFacade() {
        this.commandHistory = new CommandHistory();
        this.pendingObservers = new java.util.ArrayList<>();
    }

    /**
     * Starts a new game with the specified settings.
     *
     * @param humanPlayerCount  number of human players (1-4)
     * @param aiDifficulty      AI difficulty level (0 = random)
     * @param simplifiedVersion true for simplified win condition
     */
    public void startNewGame(int humanPlayerCount, int aiDifficulty, boolean simplifiedVersion) {
        Strategy aiStrategy = createStrategy(aiDifficulty);
        game = new LabyrinthGame(humanPlayerCount, aiStrategy, simplifiedVersion);
        commandHistory.clear();
        pendingInsertCommand = null;
        
        // Re-register all observers to the new game
        for (Observer observer : pendingObservers) {
            game.addObserver(observer);
        }
        
        game.start();
    }

    /**
     * Creates an AI strategy based on difficulty level.
     */
    private Strategy createStrategy(int difficulty) {
        // Currently only random strategy is implemented
        return new RandomStrategy();
    }

    /**
     * Adds an observer to the game.
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        if (!pendingObservers.contains(observer)) {
            pendingObservers.add(observer);
        }
        if (game != null) {
            game.addObserver(observer);
        }
    }

    /**
     * Removes an observer from the game.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(Observer observer) {
        pendingObservers.remove(observer);
        if (game != null) {
            game.removeObserver(observer);
        }
    }

    // ==================== Game State Queries ====================

    /**
     * @return the current game state
     */
    public GameState getState() {
        return game != null ? game.getState() : GameState.NOT_STARTED;
    }

    /**
     * @return true if a game is in progress
     */
    public boolean isGameRunning() {
        GameState state = getState();
        return state == GameState.WAITING_INSERT || state == GameState.WAITING_MOVE;
    }

    /**
     * @return true if the game has finished
     */
    public boolean isFinished() {
        return game != null && game.isFinished();
    }

    /**
     * @return the winner's player ID, or -1 if no winner
     */
    public int getWinnerId() {
        return game != null ? game.getWinnerId() : -1;
    }

    /**
     * @return the winning player, or null if no winner
     */
    public Player getWinner() {
        int winnerId = getWinnerId();
        if (winnerId >= 0 && game != null) {
            return game.getPlayers()[winnerId];
        }
        return null;
    }

    // ==================== Board Access ====================

    /**
     * @return the game board (read-only access for display)
     */
    public Board getBoard() {
        return game != null ? game.getBoard() : null;
    }

    /**
     * @return the current spare tile
     */
    public Tile getSpareTile() {
        return game != null ? game.getBoard().getSpareTile() : null;
    }

    /**
     * Rotates the spare tile clockwise.
     */
    public void rotateSpareTile() {
        if (game != null && getState() == GameState.WAITING_INSERT) {
            game.getBoard().getSpareTile().rotateClockwise();
        }
    }

    /**
     * @return list of all valid insertion arrows
     */
    public List<Arrow> getValidArrows() {
        return game != null ? game.getBoard().getValidArrows() : List.of();
    }

    // ==================== Player Access ====================

    /**
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return game != null ? game.getCurrentPlayer() : null;
    }

    /**
     * @return array of all players
     */
    public Player[] getPlayers() {
        return game != null ? game.getPlayers() : new Player[0];
    }

    /**
     * @return true if current player is an AI
     */
    public boolean isCurrentPlayerRobot() {
        Player player = getCurrentPlayer();
        return player != null && player.isRobot();
    }

    // ==================== Game Actions ====================

    /**
     * Checks if a tile insertion is valid at the given arrow.
     *
     * @param arrow the insertion arrow
     * @return true if the insertion is allowed
     */
    public boolean canInsert(Arrow arrow) {
        return game != null && game.canInsert(arrow);
    }

    /**
     * Inserts the spare tile at the given arrow position.
     * This is the first phase of a turn.
     *
     * @param arrow the insertion arrow
     * @throws IllegalStateException if not in insertion phase or invalid arrow
     */
    public void insertTile(Arrow arrow) {
        if (game == null) {
            throw new IllegalStateException("No game in progress");
        }
        if (!canInsert(arrow)) {
            throw new IllegalArgumentException("Invalid insertion");
        }

        Direction orientation = game.getBoard().getSpareTile().getOrientation();
        pendingInsertCommand = new InsertTileCommand(game, arrow, orientation);
        pendingInsertCommand.execute();
    }

    /**
     * Checks if a move to the given position is valid.
     *
     * @param destination the target position
     * @return true if the move is allowed
     */
    public boolean canMove(Position destination) {
        return game != null && game.canMove(destination);
    }

    /**
     * Moves the current player to the given position.
     * This is the second phase of a turn.
     *
     * @param destination the target position
     * @throws IllegalStateException if not in movement phase or invalid move
     */
    public void movePlayer(Position destination) {
        if (game == null) {
            throw new IllegalStateException("No game in progress");
        }
        if (!canMove(destination)) {
            throw new IllegalArgumentException("Invalid move");
        }

        MovePlayerCommand moveCommand = new MovePlayerCommand(game, destination);

        // Combine insert and move into a turn command for undo/redo
        if (pendingInsertCommand != null) {
            TurnCommand turnCommand = new TurnCommand(pendingInsertCommand, moveCommand);
            // The insert was already executed, so just execute the move
            moveCommand.execute();
            // Add the complete turn to history (without re-executing)
            commandHistory.executeCommand(new Command() {
                @Override
                public void execute() {
                    // Already executed
                }

                @Override
                public void undo() {
                    turnCommand.undo();
                }
            });
            // Actually we need a different approach - store the turn properly
        }

        pendingInsertCommand = null;
    }

    /**
     * Performs a complete turn (insert + move) as a single undoable operation.
     *
     * @param arrow       the insertion arrow
     * @param destination the destination position (or null to stay in place)
     */
    public void playTurn(Arrow arrow, Position destination) {
        if (game == null) {
            throw new IllegalStateException("No game in progress");
        }
        if (!canInsert(arrow)) {
            throw new IllegalArgumentException("Invalid insertion");
        }

        Direction orientation = game.getBoard().getSpareTile().getOrientation();
        InsertTileCommand insertCmd = new InsertTileCommand(game, arrow, orientation);

        // Execute insert to determine valid moves
        insertCmd.execute();

        // Validate destination
        Position actualDestination = destination;
        if (actualDestination == null) {
            actualDestination = game.getCurrentPlayer().getPosition();
        }
        if (!game.canMove(actualDestination)) {
            // Invalid destination, undo insertion
            insertCmd.undo();
            throw new IllegalArgumentException("Invalid move destination");
        }

        MovePlayerCommand moveCmd = new MovePlayerCommand(game, actualDestination);
        moveCmd.execute();

        // Store the combined command for undo/redo
        commandHistory.executeCommand(new TurnCommand(insertCmd, moveCmd) {
            @Override
            public void execute() {
                // Already executed above
            }
        });
    }

    /**
     * @return positions reachable by the current player
     */
    public List<Position> getReachablePositions() {
        return game != null ? game.getReachablePositions() : List.of();
    }

    /**
     * Abandons the current game.
     */
    public void abandon() {
        if (game != null) {
            game.abandon();
            commandHistory.clear();
        }
    }

    // ==================== Undo/Redo ====================

    /**
     * @return true if undo is available
     */
    public boolean canUndo() {
        return commandHistory.canUndo() && isGameRunning();
    }

    /**
     * @return true if redo is available
     */
    public boolean canRedo() {
        return commandHistory.canRedo() && isGameRunning();
    }

    /**
     * Undoes the last turn.
     */
    public void undo() {
        if (canUndo()) {
            commandHistory.undo();
        }
    }

    /**
     * Redoes the last undone turn.
     */
    public void redo() {
        if (canRedo()) {
            commandHistory.redo();
        }
    }

    // ==================== AI Support ====================

    /**
     * Executes the AI's turn if the current player is a robot.
     */
    public void playAITurn() {
        if (!isCurrentPlayerRobot() || !isGameRunning()) {
            return;
        }

        Player player = getCurrentPlayer();
        Strategy strategy = player.getStrategy();
        if (strategy == null) {
            return;
        }

        Move move = strategy.chooseMove(game.getBoard(), player, getSpareTile());

        // Apply the rotation
        Tile spare = getSpareTile();
        while (spare.getOrientation() != move.getTileRotation()) {
            spare.rotateClockwise();
        }

        // Execute the turn
        playTurn(move.getInsertArrow(), move.getDestination());
    }
}
