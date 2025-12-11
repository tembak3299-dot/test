package g65058.dev3.labyrinthe.model.game;

import g65058.dev3.labyrinthe.model.board.Arrow;
import g65058.dev3.labyrinthe.model.board.Direction;
import g65058.dev3.labyrinthe.model.board.Position;

import java.util.Objects;

/**
 * Represents a complete move in the game: tile insertion + player movement.
 */
public class Move {
    private final Arrow insertArrow;
    private final Direction tileRotation;
    private final Position destination;

    /**
     * Creates a new move.
     *
     * @param insertArrow  the arrow where the tile is inserted
     * @param tileRotation the rotation to apply to the spare tile before insertion
     * @param destination  the destination position for the player (null to stay in place)
     */
    public Move(Arrow insertArrow, Direction tileRotation, Position destination) {
        this.insertArrow = Objects.requireNonNull(insertArrow);
        this.tileRotation = tileRotation;
        this.destination = destination;
    }

    /**
     * @return the insertion arrow
     */
    public Arrow getInsertArrow() {
        return insertArrow;
    }

    /**
     * @return the tile rotation
     */
    public Direction getTileRotation() {
        return tileRotation;
    }

    /**
     * @return the destination position
     */
    public Position getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Move other)) return false;
        return insertArrow.equals(other.insertArrow)
                && tileRotation == other.tileRotation
                && Objects.equals(destination, other.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insertArrow, tileRotation, destination);
    }

    @Override
    public String toString() {
        return "Move{insert=" + insertArrow + ", rotation=" + tileRotation + ", dest=" + destination + "}";
    }
}
