package g65058.dev3.labyrinthe.model.game;

import g65058.dev3.labyrinthe.model.board.Objective;

import java.util.Objects;

/**
 * Represents an objective card that a player must collect.
 */
public class ObjectiveCard {
    private final Objective objective;
    private boolean reached;

    /**
     * Creates a new objective card.
     *
     * @param objective the objective on this card
     */
    public ObjectiveCard(Objective objective) {
        this.objective = Objects.requireNonNull(objective);
        this.reached = false;
    }

    /**
     * @return the objective
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * @return true if this objective has been reached
     */
    public boolean isReached() {
        return reached;
    }

    /**
     * Marks this objective as reached.
     */
    public void setReached() {
        this.reached = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ObjectiveCard other)) return false;
        return objective == other.objective;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objective);
    }

    @Override
    public String toString() {
        return objective + (reached ? " [REACHED]" : "");
    }
}
