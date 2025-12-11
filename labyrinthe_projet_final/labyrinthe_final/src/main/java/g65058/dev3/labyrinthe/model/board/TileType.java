package g65058.dev3.labyrinthe.model.board;

/**
 * Represents the three types of corridor tiles in the labyrinth.
 * <ul>
 *     <li>STRAIGHT (I): A straight corridor with two opposite openings</li>
 *     <li>CORNER (L): A corner corridor with two adjacent openings</li>
 *     <li>T_JUNCTION (T): A T-junction corridor with three openings</li>
 * </ul>
 */
public enum TileType {
    /**
     * Straight corridor (I-shape): openings on two opposite sides
     */
    STRAIGHT,

    /**
     * Corner corridor (L-shape): openings on two adjacent sides
     */
    CORNER,

    /**
     * T-junction corridor (T-shape): openings on three sides
     */
    T_JUNCTION
}
