package g65058.dev3.labyrinthe.model.game;

import g65058.dev3.labyrinthe.model.board.Objective;
import g65058.dev3.labyrinthe.model.board.Position;
import g65058.dev3.labyrinthe.model.ai.Strategy;

import java.util.Objects;
import java.util.Stack;

/**
 * Represents a player in the game.
 */
public class Player {
    private final int id;
    private final String name;
    private final PlayerColor color;
    private final Position startPosition;
    private Position position;
    private final Stack<ObjectiveCard> objectiveStack;
    private final Strategy strategy;
    private final boolean isRobot;

    /**
     * Creates a new player.
     *
     * @param id             the player ID (0-3)
     * @param name           the player name
     * @param color          the player color
     * @param objectiveStack the stack of objective cards
     * @param strategy       the AI strategy (null for human players)
     * @param isRobot        true if this is an AI player
     */
    public Player(int id, String name, PlayerColor color, Stack<ObjectiveCard> objectiveStack,
                  Strategy strategy, boolean isRobot) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.color = Objects.requireNonNull(color);
        this.startPosition = new Position(color.getStartRow(), color.getStartCol());
        this.position = startPosition;
        this.objectiveStack = Objects.requireNonNull(objectiveStack);
        this.strategy = strategy;
        this.isRobot = isRobot;
    }

    /**
     * @return the player ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return the player name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the player color
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     * @return the starting position
     */
    public Position getStartPosition() {
        return startPosition;
    }

    /**
     * @return the current position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @return true if this is an AI player
     */
    public boolean isRobot() {
        return isRobot;
    }

    /**
     * @return the AI strategy, or null for human players
     */
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * Sets the player's position.
     *
     * @param newPosition the new position
     */
    public void setPosition(Position newPosition) {
        this.position = Objects.requireNonNull(newPosition);
    }

    /**
     * Returns the current objective card (top of stack).
     *
     * @return the current objective card, or null if all objectives are completed
     */
    public ObjectiveCard getCurrentObjective() {
        return objectiveStack.isEmpty() ? null : objectiveStack.peek();
    }

    /**
     * Returns the current objective (from the top card).
     *
     * @return the current objective, or null if all objectives are completed
     */
    public Objective getCurrentObjectiveType() {
        ObjectiveCard card = getCurrentObjective();
        return card != null ? card.getObjective() : null;
    }

    /**
     * Marks the current objective as achieved and removes it from the stack.
     */
    public void achieveObjective() {
        if (!objectiveStack.isEmpty()) {
            ObjectiveCard card = objectiveStack.pop();
            card.setReached();
        }
    }

    /**
     * @return the number of objectives remaining
     */
    public int getRemainingObjectives() {
        return objectiveStack.size();
    }

    /**
     * @return true if all objectives have been completed
     */
    public boolean hasCompletedAllObjectives() {
        return objectiveStack.isEmpty();
    }

    /**
     * Checks if the player is back at their starting position.
     *
     * @return true if at start position
     */
    public boolean isAtStart() {
        return position.equals(startPosition);
    }

    /**
     * Checks if the player has won (all objectives + back to start).
     *
     * @return true if the player has won
     */
    public boolean hasWon() {
        return hasCompletedAllObjectives() && isAtStart();
    }

    /**
     * Returns a copy of the objective stack for display purposes.
     *
     * @return copy of the objective stack
     */
    @SuppressWarnings("unchecked")
    public Stack<ObjectiveCard> getObjectiveStackCopy() {
        return (Stack<ObjectiveCard>) objectiveStack.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Player other)) return false;
        return id == other.id && color == other.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, color);
    }

    @Override
    public String toString() {
        return name + " (" + color + ") at " + position;
    }
}
