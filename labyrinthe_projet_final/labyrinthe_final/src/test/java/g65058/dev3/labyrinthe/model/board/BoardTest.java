package g65058.dev3.labyrinthe.model.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Board class.
 */
class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testBoardSize() {
        assertEquals(7, board.getSize());
    }

    @Test
    void testAllTilesInitialized() {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                assertNotNull(board.getTile(row, col), "Tile at (" + row + "," + col + ") should not be null");
            }
        }
    }

    @Test
    void testSpareTileExists() {
        assertNotNull(board.getSpareTile());
    }

    @Test
    void testCornerTilesAreFixed() {
        assertTrue(board.getTile(0, 0).isFixed());
        assertTrue(board.getTile(0, 6).isFixed());
        assertTrue(board.getTile(6, 0).isFixed());
        assertTrue(board.getTile(6, 6).isFixed());
    }

    @Test
    void testCornerTilesAreCornerType() {
        assertEquals(TileType.CORNER, board.getTile(0, 0).getType());
        assertEquals(TileType.CORNER, board.getTile(0, 6).getType());
        assertEquals(TileType.CORNER, board.getTile(6, 0).getType());
        assertEquals(TileType.CORNER, board.getTile(6, 6).getType());
    }

    @Test
    void testFixedTJunctionTilesHaveObjectives() {
        // Fixed T-junctions at even positions (except corners)
        assertTrue(board.getTile(0, 2).hasObjective());
        assertTrue(board.getTile(0, 4).hasObjective());
        assertTrue(board.getTile(2, 0).hasObjective());
        assertTrue(board.getTile(2, 2).hasObjective());
    }

    @Test
    void testGetAllArrowsReturns12() {
        List<Arrow> arrows = board.getAllArrows();
        assertEquals(12, arrows.size());
    }

    @Test
    void testGetValidArrowsInitially12() {
        // No previous insertion, all 12 arrows should be valid
        List<Arrow> validArrows = board.getValidArrows();
        assertEquals(12, validArrows.size());
    }

    @Test
    void testCanInsertInitially() {
        List<Arrow> arrows = Arrow.getAllArrows();
        for (Arrow arrow : arrows) {
            assertTrue(board.canInsert(arrow));
        }
    }

    @Test
    void testInsertAndShiftChangesSpare() {
        Tile originalSpare = board.getSpareTile();
        Arrow arrow = new Arrow(new Position(0, 1), Direction.SOUTH);

        board.insertAndShift(arrow);

        Tile newSpare = board.getSpareTile();
        assertNotEquals(originalSpare, newSpare);
    }

    @Test
    void testCannotReverseInsertion() {
        Arrow arrowDown = new Arrow(new Position(0, 1), Direction.SOUTH);
        board.insertAndShift(arrowDown);

        // The opposite arrow should be forbidden
        Arrow arrowUp = new Arrow(new Position(6, 1), Direction.NORTH);
        assertFalse(board.canInsert(arrowUp));
    }

    @Test
    void testGetValidArrowsExcludesReverse() {
        Arrow arrow = new Arrow(new Position(0, 1), Direction.SOUTH);
        board.insertAndShift(arrow);

        List<Arrow> validArrows = board.getValidArrows();
        assertEquals(11, validArrows.size());

        Arrow reverse = new Arrow(new Position(6, 1), Direction.NORTH);
        assertFalse(validArrows.contains(reverse));
    }

    @Test
    void testReachablePositionsFromCorner() {
        Position corner = new Position(0, 0);
        List<Position> reachable = board.getReachablePositions(corner);

        assertNotNull(reachable);
        assertTrue(reachable.contains(corner)); // Current position is always reachable
    }

    @Test
    void testIsReachable() {
        Position start = new Position(0, 0);
        assertTrue(board.isReachable(start, start)); // Same position is reachable
    }

    @Test
    void testBoardCopy() {
        Board copy = board.copy();

        // Copy should have same structure but be independent
        assertNotSame(board, copy);

        // Modifying copy shouldn't affect original
        copy.insertAndShift(new Arrow(new Position(0, 1), Direction.SOUTH));
        assertNull(board.getLastArrow());
    }

    @Test
    void testInsertShiftsSouth() {
        // Track a tile in column 1
        Tile topTile = board.getTile(0, 1);

        Arrow arrow = new Arrow(new Position(0, 1), Direction.SOUTH);
        board.insertAndShift(arrow);

        // The top tile should have moved down
        assertEquals(topTile, board.getTile(1, 1));
    }

    @Test
    void testInsertShiftsNorth() {
        Tile bottomTile = board.getTile(6, 1);

        Arrow arrow = new Arrow(new Position(6, 1), Direction.NORTH);
        board.insertAndShift(arrow);

        assertEquals(bottomTile, board.getTile(5, 1));
    }

    @Test
    void testInsertShiftsEast() {
        Tile leftTile = board.getTile(1, 0);

        Arrow arrow = new Arrow(new Position(1, 0), Direction.EAST);
        board.insertAndShift(arrow);

        assertEquals(leftTile, board.getTile(1, 1));
    }

    @Test
    void testInsertShiftsWest() {
        Tile rightTile = board.getTile(1, 6);

        Arrow arrow = new Arrow(new Position(1, 6), Direction.WEST);
        board.insertAndShift(arrow);

        assertEquals(rightTile, board.getTile(1, 5));
    }
}
