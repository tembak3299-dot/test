package g65058.dev3.labyrinthe.model.game;

import g65058.dev3.labyrinthe.model.ai.RandomStrategy;
import g65058.dev3.labyrinthe.model.board.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the LabyrinthGame class.
 */
class LabyrinthGameTest {
    private LabyrinthGame game;

    @BeforeEach
    void setUp() {
        game = new LabyrinthGame(1, new RandomStrategy(), true);
    }

    @Test
    void testInitialStateNotStarted() {
        assertEquals(GameState.NOT_STARTED, game.getState());
    }

    @Test
    void testStartChangesState() {
        game.start();
        assertEquals(GameState.WAITING_INSERT, game.getState());
    }

    @Test
    void testFourPlayersCreated() {
        assertEquals(4, game.getPlayers().length);
    }

    @Test
    void testFirstPlayerIsHuman() {
        assertFalse(game.getPlayers()[0].isRobot());
    }

    @Test
    void testOtherPlayersAreRobots() {
        assertTrue(game.getPlayers()[1].isRobot());
        assertTrue(game.getPlayers()[2].isRobot());
        assertTrue(game.getPlayers()[3].isRobot());
    }

    @Test
    void testCurrentPlayerIsFirstAfterStart() {
        game.start();
        assertEquals(game.getPlayers()[0], game.getCurrentPlayer());
    }

    @Test
    void testCanInsertOnlyInInsertPhase() {
        game.start();
        Arrow arrow = Arrow.getAllArrows().get(0);
        assertTrue(game.canInsert(arrow));
    }

    @Test
    void testCannotInsertBeforeStart() {
        Arrow arrow = Arrow.getAllArrows().get(0);
        assertFalse(game.canInsert(arrow));
    }

    @Test
    void testCannotMoveInInsertPhase() {
        game.start();
        Position pos = new Position(0, 0);
        assertFalse(game.canMove(pos));
    }

    @Test
    void testStateChangesAfterInsertion() {
        game.start();
        Arrow arrow = Arrow.getAllArrows().get(0);
        Direction orientation = game.getBoard().getSpareTile().getOrientation();

        game.performInsertion(arrow, orientation);

        assertEquals(GameState.WAITING_MOVE, game.getState());
    }

    @Test
    void testCanMoveAfterInsertion() {
        game.start();
        Arrow arrow = Arrow.getAllArrows().get(0);
        Direction orientation = game.getBoard().getSpareTile().getOrientation();
        game.performInsertion(arrow, orientation);

        // Current player should be able to move to at least their current position
        Position currentPos = game.getCurrentPlayer().getPosition();
        assertTrue(game.canMove(currentPos));
    }

    @Test
    void testPlayersStartAtCorners() {
        game.start();
        Player[] players = game.getPlayers();

        assertEquals(new Position(0, 0), players[0].getPosition());
        assertEquals(new Position(0, 6), players[1].getPosition());
        assertEquals(new Position(6, 6), players[2].getPosition());
        assertEquals(new Position(6, 0), players[3].getPosition());
    }

    @Test
    void testPlayersHaveObjectives() {
        game.start();
        for (Player player : game.getPlayers()) {
            assertTrue(player.getRemainingObjectives() > 0);
        }
    }

    @Test
    void testNextPlayerAfterMove() {
        game.start();
        Arrow arrow = Arrow.getAllArrows().get(0);
        Direction orientation = game.getBoard().getSpareTile().getOrientation();
        game.performInsertion(arrow, orientation);

        Player firstPlayer = game.getCurrentPlayer();
        Position currentPos = firstPlayer.getPosition();
        game.performMove(currentPos);

        // Should be next player's turn
        assertEquals(GameState.WAITING_INSERT, game.getState());
        assertNotEquals(firstPlayer, game.getCurrentPlayer());
    }

    @Test
    void testAbandonSetsStateAborted() {
        game.start();
        game.abandon();
        assertEquals(GameState.ABORTED, game.getState());
    }

    @Test
    void testIsFinishedAfterAbandon() {
        game.start();
        game.abandon();
        assertTrue(game.isFinished());
    }

    @Test
    void testWinnerIdIsMinusOneInitially() {
        game.start();
        assertEquals(-1, game.getWinnerId());
    }

    @Test
    void testBoardIsNotNull() {
        assertNotNull(game.getBoard());
    }

    @Test
    void testReachablePositionsNotEmptyAfterInsertion() {
        game.start();
        Arrow arrow = Arrow.getAllArrows().get(0);
        Direction orientation = game.getBoard().getSpareTile().getOrientation();
        game.performInsertion(arrow, orientation);

        assertFalse(game.getReachablePositions().isEmpty());
    }

    @Test
    void testPlayerPositionUpdatedAfterMove() {
        game.start();
        Arrow arrow = Arrow.getAllArrows().get(0);
        Direction orientation = game.getBoard().getSpareTile().getOrientation();
        game.performInsertion(arrow, orientation);

        Player player = game.getCurrentPlayer();
        Position newPos = game.getReachablePositions().get(0);
        game.performMove(newPos);

        // The player who just moved should now be at the new position
        // Note: after move, current player changes, so we check the previous player
        assertEquals(newPos, game.getPlayers()[0].getPosition());
    }
}
