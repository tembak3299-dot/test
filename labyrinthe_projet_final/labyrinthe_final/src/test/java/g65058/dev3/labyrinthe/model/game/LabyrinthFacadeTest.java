package g65058.dev3.labyrinthe.model.game;

import g65058.dev3.labyrinthe.model.board.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the LabyrinthFacade class.
 */
class LabyrinthFacadeTest {
    private LabyrinthFacade facade;

    @BeforeEach
    void setUp() {
        facade = new LabyrinthFacade();
    }

    @Test
    void testInitialStateNotStarted() {
        assertEquals(GameState.NOT_STARTED, facade.getState());
    }

    @Test
    void testStartNewGame() {
        facade.startNewGame(1, 0, true);
        assertEquals(GameState.WAITING_INSERT, facade.getState());
    }

    @Test
    void testIsGameRunningAfterStart() {
        facade.startNewGame(1, 0, true);
        assertTrue(facade.isGameRunning());
    }

    @Test
    void testNotGameRunningBeforeStart() {
        assertFalse(facade.isGameRunning());
    }

    @Test
    void testGetBoardAfterStart() {
        facade.startNewGame(1, 0, true);
        assertNotNull(facade.getBoard());
    }

    @Test
    void testGetSpareTileAfterStart() {
        facade.startNewGame(1, 0, true);
        assertNotNull(facade.getSpareTile());
    }

    @Test
    void testGetPlayersAfterStart() {
        facade.startNewGame(1, 0, true);
        Player[] players = facade.getPlayers();
        assertEquals(4, players.length);
    }

    @Test
    void testGetCurrentPlayerAfterStart() {
        facade.startNewGame(1, 0, true);
        assertNotNull(facade.getCurrentPlayer());
    }

    @Test
    void testFirstPlayerIsHuman() {
        facade.startNewGame(1, 0, true);
        assertFalse(facade.isCurrentPlayerRobot());
    }

    @Test
    void testGetValidArrows() {
        facade.startNewGame(1, 0, true);
        List<Arrow> arrows = facade.getValidArrows();
        assertEquals(12, arrows.size());
    }

    @Test
    void testCanInsertValidArrow() {
        facade.startNewGame(1, 0, true);
        Arrow arrow = facade.getValidArrows().get(0);
        assertTrue(facade.canInsert(arrow));
    }

    @Test
    void testInsertTileChangesState() {
        facade.startNewGame(1, 0, true);
        Arrow arrow = facade.getValidArrows().get(0);

        facade.insertTile(arrow);

        assertEquals(GameState.WAITING_MOVE, facade.getState());
    }

    @Test
    void testCanMoveAfterInsert() {
        facade.startNewGame(1, 0, true);
        Arrow arrow = facade.getValidArrows().get(0);
        facade.insertTile(arrow);

        List<Position> reachable = facade.getReachablePositions();
        assertFalse(reachable.isEmpty());

        Position destination = reachable.get(0);
        assertTrue(facade.canMove(destination));
    }

    @Test
    void testRotateSpareTile() {
        facade.startNewGame(1, 0, true);
        Direction originalOrientation = facade.getSpareTile().getOrientation();

        facade.rotateSpareTile();

        Direction newOrientation = facade.getSpareTile().getOrientation();
        assertEquals(originalOrientation.rotateClockwise(), newOrientation);
    }

    @Test
    void testAbandonGame() {
        facade.startNewGame(1, 0, true);
        facade.abandon();

        assertFalse(facade.isGameRunning());
        assertTrue(facade.isFinished());
    }

    @Test
    void testUndoNotAvailableInitially() {
        facade.startNewGame(1, 0, true);
        assertFalse(facade.canUndo());
    }

    @Test
    void testRedoNotAvailableInitially() {
        facade.startNewGame(1, 0, true);
        assertFalse(facade.canRedo());
    }

    @Test
    void testWinnerIsNullBeforeGameEnd() {
        facade.startNewGame(1, 0, true);
        assertNull(facade.getWinner());
        assertEquals(-1, facade.getWinnerId());
    }

    @Test
    void testPlayersHaveObjectives() {
        facade.startNewGame(1, 0, true);
        for (Player player : facade.getPlayers()) {
            assertTrue(player.getRemainingObjectives() > 0);
        }
    }

    @Test
    void testPlayersAtStartPositions() {
        facade.startNewGame(1, 0, true);
        Player[] players = facade.getPlayers();

        assertEquals(new Position(0, 0), players[0].getPosition());
        assertEquals(new Position(0, 6), players[1].getPosition());
        assertEquals(new Position(6, 6), players[2].getPosition());
        assertEquals(new Position(6, 0), players[3].getPosition());
    }

    @Test
    void testCannotInsertDuringMovePhase() {
        facade.startNewGame(1, 0, true);
        Arrow arrow = facade.getValidArrows().get(0);
        facade.insertTile(arrow);

        // Now in WAITING_MOVE, should not be able to insert
        Arrow anotherArrow = facade.getValidArrows().get(1);
        assertFalse(facade.canInsert(anotherArrow));
    }

    @Test
    void testCannotMoveDuringInsertPhase() {
        facade.startNewGame(1, 0, true);
        Position anyPosition = new Position(0, 0);

        assertFalse(facade.canMove(anyPosition));
    }

    @Test
    void testFourPlayersCreated() {
        facade.startNewGame(2, 0, true);
        assertEquals(4, facade.getPlayers().length);

        // First two are human, last two are robots
        assertFalse(facade.getPlayers()[0].isRobot());
        assertFalse(facade.getPlayers()[1].isRobot());
        assertTrue(facade.getPlayers()[2].isRobot());
        assertTrue(facade.getPlayers()[3].isRobot());
    }
}
