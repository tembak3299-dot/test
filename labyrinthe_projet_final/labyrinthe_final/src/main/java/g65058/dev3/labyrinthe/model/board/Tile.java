package g65058.dev3.labyrinthe.model.board;

import java.util.Objects;

/**
 * Represents a corridor tile in the labyrinth.
 * Each tile has a type (I, L, or T), an orientation, optionally an objective,
 * and can be fixed (immovable) or mobile.
 */
public class Tile {
    private final TileType type;
    private Direction orientation;
    private final Objective objective;
    private final boolean fixed;

    /**
     * Creates a new tile.
     *
     * @param type        the tile type (STRAIGHT, CORNER, or T_JUNCTION)
     * @param orientation the direction the tile is facing
     * @param objective   the objective on this tile, or null if none
     * @param fixed       true if the tile cannot be moved
     */
    public Tile(TileType type, Direction orientation, Objective objective, boolean fixed) {
        this.type = Objects.requireNonNull(type);
        this.orientation = Objects.requireNonNull(orientation);
        this.objective = objective;
        this.fixed = fixed;
    }

    /**
     * @return the tile type
     */
    public TileType getType() {
        return type;
    }

    /**
     * @return the tile orientation
     */
    public Direction getOrientation() {
        return orientation;
    }

    /**
     * @return the objective on this tile, or null if none
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * @return true if this tile cannot be moved
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * @return true if this tile has an objective
     */
    public boolean hasObjective() {
        return objective != null;
    }

    /**
     * Rotates the tile 90 degrees clockwise.
     * Has no effect on fixed tiles.
     */
    public void rotateClockwise() {
        if (!fixed) {
            orientation = orientation.rotateClockwise();
        }
    }

    /**
     * Sets the orientation of this tile (only for mobile tiles).
     *
     * @param newOrientation the new orientation
     */
    public void setOrientation(Direction newOrientation) {
        if (!fixed) {
            this.orientation = newOrientation;
        }
    }

    /**
     * Checks if this tile has an opening in the given direction.
     *
     * @param direction the direction to check
     * @return true if the tile has an opening in that direction
     */
    public boolean isOpenTo(Direction direction) {
        return !hasWallAt(direction);
    }

    /**
     * Checks if this tile has a wall (no opening) in the given direction.
     *
     * @param direction the direction to check
     * @return true if there is a wall in that direction
     */
    public boolean hasWallAt(Direction direction) {
        return switch (type) {
            case STRAIGHT -> hasWallAtStraight(direction);
            case CORNER -> hasWallAtCorner(direction);
            case T_JUNCTION -> hasWallAtTJunction(direction);
        };
    }

    /**
     * Straight tile (I): walls on the perpendicular sides.
     * E.g., orientation NORTH means openings N/S, walls E/W.
     */
    private boolean hasWallAtStraight(Direction direction) {
        Direction wall1 = orientation.rotate(1); // 90 degrees clockwise
        Direction wall2 = orientation.rotate(3); // 270 degrees clockwise (= 90 counter-clockwise)
        return direction == wall1 || direction == wall2;
    }

    /**
     * Corner tile (L): two adjacent openings.
     * Orientation indicates one opening, the next clockwise is the other opening.
     */
    private boolean hasWallAtCorner(Direction direction) {
        Direction wall1 = orientation.rotate(2); // opposite
        Direction wall2 = orientation.rotate(3); // 270 degrees
        return direction == wall1 || direction == wall2;
    }

    /**
     * T-junction tile (T): three openings, one wall.
     * The wall is opposite to the orientation.
     */
    private boolean hasWallAtTJunction(Direction direction) {
        Direction wall = orientation.rotate(2); // opposite direction
        return direction == wall;
    }

    /**
     * Creates a deep copy of this tile.
     *
     * @return a copy of this tile
     */
    public Tile copy() {
        return new Tile(type, orientation, objective, fixed);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tile other)) return false;
        return type == other.type
                && orientation == other.orientation
                && objective == other.objective
                && fixed == other.fixed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, orientation, objective, fixed);
    }

    @Override
    public String toString() {
        String result = type + " " + orientation;
        if (objective != null) {
            result += " (" + objective + ")";
        }
        if (fixed) {
            result += " [FIXED]";
        }
        return result;
    }
}
