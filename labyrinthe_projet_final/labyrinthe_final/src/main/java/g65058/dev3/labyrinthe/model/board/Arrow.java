package g65058.dev3.labyrinthe.model.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an insertion arrow position on the board edge.
 * The game has 12 arrows (3 per side) where the spare tile can be inserted.
 * Each arrow indicates both the position and the direction of insertion.
 */
public class Arrow {
    private final Position position;
    private final Direction insertDirection;

    /**
     * Creates a new arrow.
     *
     * @param position        the edge position where the tile is inserted
     * @param insertDirection the direction the tile pushes (towards the interior)
     */
    public Arrow(Position position, Direction insertDirection) {
        this.position = Objects.requireNonNull(position);
        this.insertDirection = Objects.requireNonNull(insertDirection);
    }

    /**
     * @return the insertion position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @return the direction tiles are pushed
     */
    public Direction getInsertDirection() {
        return insertDirection;
    }

    /**
     * Returns the row index affected by this arrow.
     *
     * @return the row for horizontal insertion, or -1 for vertical
     */
    public int getRow() {
        return position.getRow();
    }

    /**
     * Returns the column index affected by this arrow.
     *
     * @return the column for vertical insertion, or -1 for horizontal
     */
    public int getCol() {
        return position.getCol();
    }

    /**
     * Checks if this arrow affects a row (horizontal insertion).
     *
     * @return true for EAST/WEST insertion directions
     */
    public boolean isHorizontal() {
        return insertDirection == Direction.EAST || insertDirection == Direction.WEST;
    }

    /**
     * Returns the opposite arrow (the arrow that would undo this insertion).
     *
     * @param boardSize the size of the board
     * @return the opposite arrow
     */
    public Arrow getOpposite(int boardSize) {
        int oppRow = position.getRow();
        int oppCol = position.getCol();

        switch (insertDirection) {
            case SOUTH -> oppRow = boardSize - 1;
            case NORTH -> oppRow = 0;
            case EAST -> oppCol = boardSize - 1;
            case WEST -> oppCol = 0;
        }

        return new Arrow(
                new Position(oppRow, oppCol),
                insertDirection.opposite()
        );
    }

    /**
     * Creates all 12 valid arrows for a standard 7x7 board.
     *
     * @return list of all insertion arrows
     */
    public static List<Arrow> getAllArrows() {
        List<Arrow> arrows = new ArrayList<>();

        // Top row (push down) - columns 1, 3, 5
        for (int col = 1; col < 7; col += 2) {
            arrows.add(new Arrow(new Position(0, col), Direction.SOUTH));
        }

        // Bottom row (push up) - columns 1, 3, 5
        for (int col = 1; col < 7; col += 2) {
            arrows.add(new Arrow(new Position(6, col), Direction.NORTH));
        }

        // Left column (push right) - rows 1, 3, 5
        for (int row = 1; row < 7; row += 2) {
            arrows.add(new Arrow(new Position(row, 0), Direction.EAST));
        }

        // Right column (push left) - rows 1, 3, 5
        for (int row = 1; row < 7; row += 2) {
            arrows.add(new Arrow(new Position(row, 6), Direction.WEST));
        }

        return arrows;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Arrow other)) return false;
        return position.equals(other.position) && insertDirection == other.insertDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, insertDirection);
    }

    @Override
    public String toString() {
        return "Arrow{" + position + " -> " + insertDirection + "}";
    }
}
