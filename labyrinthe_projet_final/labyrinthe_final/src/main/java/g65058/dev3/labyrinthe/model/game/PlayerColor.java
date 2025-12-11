package g65058.dev3.labyrinthe.model.game;

/**
 * Represents the four player colors matching the corner positions.
 */
public enum PlayerColor {
    /**
     * Yellow player - starts at top-left (0,0)
     */
    YELLOW(0, 0),

    /**
     * Blue player - starts at top-right (0,6)
     */
    BLUE(0, 6),

    /**
     * Green player - starts at bottom-right (6,6)
     */
    GREEN(6, 6),

    /**
     * Red player - starts at bottom-left (6,0)
     */
    RED(6, 0);

    private final int startRow;
    private final int startCol;

    PlayerColor(int startRow, int startCol) {
        this.startRow = startRow;
        this.startCol = startCol;
    }

    /**
     * @return the starting row for this color
     */
    public int getStartRow() {
        return startRow;
    }

    /**
     * @return the starting column for this color
     */
    public int getStartCol() {
        return startCol;
    }
}
