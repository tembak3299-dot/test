package g65058.dev3.labyrinthe.model.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Tile class.
 */
class TileTest {

    @Test
    void testCreateTile() {
        Tile tile = new Tile(TileType.CORNER, Direction.NORTH, null, false);
        assertEquals(TileType.CORNER, tile.getType());
        assertEquals(Direction.NORTH, tile.getOrientation());
        assertNull(tile.getObjective());
        assertFalse(tile.isFixed());
    }

    @Test
    void testTileWithObjective() {
        Tile tile = new Tile(TileType.T_JUNCTION, Direction.SOUTH, Objective.DRAGON, true);
        assertTrue(tile.hasObjective());
        assertEquals(Objective.DRAGON, tile.getObjective());
    }

    @Test
    void testRotateClockwise() {
        Tile tile = new Tile(TileType.CORNER, Direction.NORTH, null, false);
        tile.rotateClockwise();
        assertEquals(Direction.EAST, tile.getOrientation());

        tile.rotateClockwise();
        assertEquals(Direction.SOUTH, tile.getOrientation());

        tile.rotateClockwise();
        assertEquals(Direction.WEST, tile.getOrientation());

        tile.rotateClockwise();
        assertEquals(Direction.NORTH, tile.getOrientation());
    }

    @Test
    void testFixedTileDoesNotRotate() {
        Tile tile = new Tile(TileType.CORNER, Direction.NORTH, null, true);
        tile.rotateClockwise();
        assertEquals(Direction.NORTH, tile.getOrientation());
    }

    @Test
    void testStraightTileWalls() {
        // Straight tile oriented NORTH has openings N/S, walls E/W
        Tile tile = new Tile(TileType.STRAIGHT, Direction.NORTH, null, false);

        assertFalse(tile.hasWallAt(Direction.NORTH)); // Opening
        assertFalse(tile.hasWallAt(Direction.SOUTH)); // Opening
        assertTrue(tile.hasWallAt(Direction.EAST));   // Wall
        assertTrue(tile.hasWallAt(Direction.WEST));   // Wall
    }

    @Test
    void testCornerTileWalls() {
        // Corner tile oriented NORTH has openings N/E, walls S/W
        Tile tile = new Tile(TileType.CORNER, Direction.NORTH, null, false);

        assertFalse(tile.hasWallAt(Direction.NORTH)); // Opening
        assertFalse(tile.hasWallAt(Direction.EAST));  // Opening
        assertTrue(tile.hasWallAt(Direction.SOUTH));  // Wall
        assertTrue(tile.hasWallAt(Direction.WEST));   // Wall
    }

    @Test
    void testTJunctionTileWalls() {
        // T-junction oriented NORTH has opening N/E/W, wall S
        Tile tile = new Tile(TileType.T_JUNCTION, Direction.NORTH, null, false);

        assertFalse(tile.hasWallAt(Direction.NORTH)); // Opening
        assertFalse(tile.hasWallAt(Direction.EAST));  // Opening
        assertFalse(tile.hasWallAt(Direction.WEST));  // Opening
        assertTrue(tile.hasWallAt(Direction.SOUTH));  // Wall (opposite of orientation)
    }

    @Test
    void testIsOpenTo() {
        Tile tile = new Tile(TileType.STRAIGHT, Direction.NORTH, null, false);
        assertTrue(tile.isOpenTo(Direction.NORTH));
        assertTrue(tile.isOpenTo(Direction.SOUTH));
        assertFalse(tile.isOpenTo(Direction.EAST));
        assertFalse(tile.isOpenTo(Direction.WEST));
    }

    @Test
    void testTileCopy() {
        Tile original = new Tile(TileType.CORNER, Direction.EAST, Objective.GHOST, false);
        Tile copy = original.copy();

        assertEquals(original, copy);
        assertNotSame(original, copy);

        // Modifying copy shouldn't affect original
        copy.rotateClockwise();
        assertNotEquals(original.getOrientation(), copy.getOrientation());
    }

    @Test
    void testTileEquality() {
        Tile tile1 = new Tile(TileType.CORNER, Direction.NORTH, Objective.DRAGON, false);
        Tile tile2 = new Tile(TileType.CORNER, Direction.NORTH, Objective.DRAGON, false);
        Tile tile3 = new Tile(TileType.CORNER, Direction.EAST, Objective.DRAGON, false);

        assertEquals(tile1, tile2);
        assertNotEquals(tile1, tile3);
    }

    @Test
    void testRotatedStraightTileWalls() {
        Tile tile = new Tile(TileType.STRAIGHT, Direction.NORTH, null, false);
        tile.rotateClockwise(); // Now oriented EAST

        // After rotation: openings E/W, walls N/S
        assertFalse(tile.hasWallAt(Direction.EAST));
        assertFalse(tile.hasWallAt(Direction.WEST));
        assertTrue(tile.hasWallAt(Direction.NORTH));
        assertTrue(tile.hasWallAt(Direction.SOUTH));
    }

    @Test
    void testRotatedCornerTileWalls() {
        Tile tile = new Tile(TileType.CORNER, Direction.NORTH, null, false);
        tile.rotateClockwise(); // Now oriented EAST

        // After rotation: openings E/S, walls W/N
        assertFalse(tile.hasWallAt(Direction.EAST));
        assertFalse(tile.hasWallAt(Direction.SOUTH));
        assertTrue(tile.hasWallAt(Direction.WEST));
        assertTrue(tile.hasWallAt(Direction.NORTH));
    }
}
