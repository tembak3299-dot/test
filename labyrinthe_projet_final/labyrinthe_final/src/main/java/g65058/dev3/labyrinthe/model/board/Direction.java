package g65058.dev3.labyrinthe.model.board;

/**
 * Represents the four cardinal directions for tile connections and player movement.
 */
public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    /**
     * Returns the opposite direction.
     *
     * @return the opposite direction
     */
    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    /**
     * Rotates the direction clockwise by 90 degrees.
     *
     * @return the rotated direction
     */
    public Direction rotateClockwise() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

    /**
     * Rotates the direction by the given number of 90-degree clockwise turns.
     *
     * @param times number of 90-degree clockwise rotations
     * @return the rotated direction
     */
    public Direction rotate(int times) {
        Direction result = this;
        int normalizedTimes = ((times % 4) + 4) % 4;
        for (int i = 0; i < normalizedTimes; i++) {
            result = result.rotateClockwise();
        }
        return result;
    }
}
