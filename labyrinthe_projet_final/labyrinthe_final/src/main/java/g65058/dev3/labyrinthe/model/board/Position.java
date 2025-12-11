package g65058.dev3.labyrinthe.model.board;

import java.util.Objects;

/**
 * Represents a position on the game board with row and column coordinates.
 */
public class Position {
    private final int row;
    private final int col;

    /**
     * Creates a new position.
     *
     * @param row the row index (0-based)
     * @param col the column index (0-based)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return the row index
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column index
     */
    public int getCol() {
        return col;
    }

    /**
     * Checks if this position is within the board boundaries.
     *
     * @param size the board size
     * @return true if the position is valid
     */
    public boolean isInBounds(int size) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    /**
     * Returns the position obtained by moving in the given direction.
     *
     * @param direction the direction to move
     * @return the new position
     */
    public Position move(Direction direction) {
        return switch (direction) {
            case NORTH -> new Position(row - 1, col);
            case SOUTH -> new Position(row + 1, col);
            case EAST -> new Position(row, col + 1);
            case WEST -> new Position(row, col - 1);
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position other)) return false;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
