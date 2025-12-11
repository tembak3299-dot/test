package g65058.dev3.labyrinthe.model.game;

import g65058.dev3.labyrinthe.model.board.*;
import g65058.dev3.labyrinthe.model.ai.Strategy;
import g65058.dev3.labyrinthe.model.observer.Observable;
import g65058.dev3.labyrinthe.model.observer.Observer;

import java.util.*;

/**
 * Main game logic class for the Labyrinth game.
 * Implements Observable to notify views of state changes.
 */
public class LabyrinthGame implements Observable {
    private Board board;
    private final Player[] players;
    private int currentPlayerIndex;
    private GameState state;
    private int winnerId;
    private final List<Observer> observers;
    private final boolean simplifiedVersion;

    /**
     * Creates a new game with the specified number of human players.
     *
     * @param humanPlayerCount number of human players (1-4)
     * @param aiStrategy       strategy for AI players
     * @param simplifiedVersion true for simplified end condition (no return to start)
     */
    public LabyrinthGame(int humanPlayerCount, Strategy aiStrategy, boolean simplifiedVersion) {
        if (humanPlayerCount < 1 || humanPlayerCount > 4) {
            throw new IllegalArgumentException("Must have 1-4 players");
        }

        this.board = new Board();
        this.players = new Player[4];
        this.currentPlayerIndex = 0;
        this.state = GameState.NOT_STARTED;
        this.winnerId = -1;
        this.observers = new ArrayList<>();
        this.simplifiedVersion = simplifiedVersion;

        initializePlayers(humanPlayerCount, aiStrategy);
    }

    /**
     * Initializes players with their objective cards.
     */
    private void initializePlayers(int humanCount, Strategy aiStrategy) {
        // Create and shuffle objective cards
        List<ObjectiveCard> allCards = new ArrayList<>();
        for (Objective obj : Objective.values()) {
            allCards.add(new ObjectiveCard(obj));
        }
        Collections.shuffle(allCards);

        // Distribute cards equally (6 cards each for 4 players)
        int cardsPerPlayer = allCards.size() / 4;

        PlayerColor[] colors = PlayerColor.values();
        String[] names = {"Player 1", "Player 2", "Player 3", "Player 4"};

        for (int i = 0; i < 4; i++) {
            Stack<ObjectiveCard> playerCards = new Stack<>();
            for (int j = 0; j < cardsPerPlayer; j++) {
                playerCards.push(allCards.remove(0));
            }

            boolean isRobot = i >= humanCount;
            Strategy strategy = isRobot ? aiStrategy : null;

            players[i] = new Player(i, names[i], colors[i], playerCards, strategy, isRobot);
        }
    }

    /**
     * Starts a new game.
     */
    public void start() {
        state = GameState.WAITING_INSERT;
        currentPlayerIndex = 0;
        winnerId = -1;
        notifyObservers();
    }

    /**
     * @return the current game state
     */
    public GameState getState() {
        return state;
    }

    /**
     * @return the game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return array of all players
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    /**
     * @return the current player index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Sets the current player index (for undo).
     */
    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    /**
     * @return the ID of the winner, or -1 if no winner yet
     */
    public int getWinnerId() {
        return winnerId;
    }

    /**
     * @return true if the game has finished
     */
    public boolean isFinished() {
        return state == GameState.FINISHED || state == GameState.ABORTED;
    }

    /**
     * Checks if a tile insertion is valid.
     *
     * @param arrow the insertion arrow
     * @return true if the insertion is allowed
     */
    public boolean canInsert(Arrow arrow) {
        return state == GameState.WAITING_INSERT && board.canInsert(arrow);
    }

    /**
     * Checks if a move to the given position is valid.
     *
     * @param destination the target position
     * @return true if the move is allowed
     */
    public boolean canMove(Position destination) {
        if (state != GameState.WAITING_MOVE) {
            return false;
        }
        Player player = getCurrentPlayer();
        return board.getReachablePositions(player.getPosition()).contains(destination);
    }

    /**
     * Performs a tile insertion (called by Command).
     *
     * @param arrow       the insertion arrow
     * @param orientation the orientation for the spare tile
     */
    public void performInsertion(Arrow arrow, Direction orientation) {
        // Set spare tile orientation
        board.getSpareTile().setOrientation(orientation);

        // Update player positions if they are on the affected row/column
        updatePlayerPositionsForShift(arrow);

        // Perform the insertion
        board.insertAndShift(arrow);

        state = GameState.WAITING_MOVE;
        notifyObservers();
    }

    /**
     * Updates player positions when a row/column shifts.
     */
    private void updatePlayerPositionsForShift(Arrow arrow) {
        int size = Board.SIZE;
        Direction dir = arrow.getInsertDirection();

        for (Player player : players) {
            Position pos = player.getPosition();
            int row = pos.getRow();
            int col = pos.getCol();

            boolean affected = false;
            int newRow = row;
            int newCol = col;

            if (arrow.isHorizontal() && row == arrow.getRow()) {
                // Horizontal shift
                if (dir == Direction.EAST) {
                    newCol = col + 1;
                    if (newCol >= size) {
                        newCol = 0; // Wrap to inserted tile position
                    }
                } else { // WEST
                    newCol = col - 1;
                    if (newCol < 0) {
                        newCol = size - 1;
                    }
                }
                affected = true;
            } else if (!arrow.isHorizontal() && col == arrow.getCol()) {
                // Vertical shift
                if (dir == Direction.SOUTH) {
                    newRow = row + 1;
                    if (newRow >= size) {
                        newRow = 0;
                    }
                } else { // NORTH
                    newRow = row - 1;
                    if (newRow < 0) {
                        newRow = size - 1;
                    }
                }
                affected = true;
            }

            if (affected) {
                player.setPosition(new Position(newRow, newCol));
            }
        }
    }

    /**
     * Performs a player move (called by Command).
     *
     * @param destination the destination position
     */
    public void performMove(Position destination) {
        Player player = getCurrentPlayer();
        player.setPosition(destination);

        // Check if objective is achieved
        Tile tile = board.getTile(destination);
        if (tile.hasObjective()) {
            Objective tileObjective = tile.getObjective();
            Objective playerObjective = player.getCurrentObjectiveType();
            if (tileObjective == playerObjective) {
                player.achieveObjective();
            }
        }

        // Check for win condition
        if (checkWinCondition(player)) {
            winnerId = player.getId();
            state = GameState.FINISHED;
        } else {
            // Next player's turn
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            state = GameState.WAITING_INSERT;
        }

        notifyObservers();
    }

    /**
     * Checks if a move would achieve the current objective.
     */
    public boolean wouldAchieveObjective(Position destination) {
        Player player = getCurrentPlayer();
        Tile tile = board.getTile(destination);
        if (!tile.hasObjective()) {
            return false;
        }
        return tile.getObjective() == player.getCurrentObjectiveType();
    }

    /**
     * Checks if a player has won.
     */
    private boolean checkWinCondition(Player player) {
        if (simplifiedVersion) {
            // Simplified: just complete all objectives
            return player.hasCompletedAllObjectives();
        } else {
            // Standard: complete objectives AND return to start
            return player.hasWon();
        }
    }

    /**
     * Returns positions reachable by the current player.
     *
     * @return list of reachable positions
     */
    public List<Position> getReachablePositions() {
        if (state != GameState.WAITING_MOVE) {
            return Collections.emptyList();
        }
        return board.getReachablePositions(getCurrentPlayer().getPosition());
    }

    /**
     * Abandons the current game.
     */
    public void abandon() {
        state = GameState.ABORTED;
        notifyObservers();
    }

    /**
     * Restores board state (for undo).
     */
    public void restoreBoardState(Tile[][] tiles, Tile spareTile, Arrow lastArrow) {
        this.board = new Board(tiles, spareTile, lastArrow);
        state = GameState.WAITING_INSERT;
        notifyObservers();
    }

    /**
     * Restores an objective to a player's stack (for undo).
     */
    public void restoreObjective(Player player, ObjectiveCard card) {
        player.getObjectiveStackCopy().push(card);
    }

    /**
     * Sets the game state to WAITING_MOVE (for undo).
     */
    public void setStateToWaitingMove() {
        this.state = GameState.WAITING_MOVE;
        notifyObservers();
    }

    // Observer pattern methods

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers of a state change.
     * This method is private to ensure only the model triggers notifications.
     */
    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
