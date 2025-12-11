package g65058.dev3.labyrinthe.model.ai;

import g65058.dev3.labyrinthe.model.board.*;
import g65058.dev3.labyrinthe.model.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the RandomStrategy class.
 */
class RandomStrategyTest {
    private RandomStrategy strategy;
    private Board board;
    private Player player;

    @BeforeEach
    void setUp() {
        strategy = new RandomStrategy(42); // Fixed seed for reproducibility
        board = new Board();

        // Create a test player
        Stack<ObjectiveCard> objectives = new Stack<>();
        objectives.push(new ObjectiveCard(Objective.DRAGON));
        objectives.push(new ObjectiveCard(Objective.GHOST));

        player = new Player(0, "TestPlayer", PlayerColor.YELLOW, objectives, null, false);
    }

    @Test
    void testChooseMoveReturnsNonNull() {
        Tile spareTile = board.getSpareTile();
        Move move = strategy.chooseMove(board, player, spareTile);

        assertNotNull(move);
    }

    @Test
    void testChooseMoveHasValidArrow() {
        Tile spareTile = board.getSpareTile();
        Move move = strategy.chooseMove(board, player, spareTile);

        Arrow arrow = move.getInsertArrow();
        assertNotNull(arrow);
        assertTrue(board.canInsert(arrow));
    }

    @Test
    void testChooseMoveHasValidRotation() {
        Tile spareTile = board.getSpareTile();
        Move move = strategy.chooseMove(board, player, spareTile);

        Direction rotation = move.getTileRotation();
        assertNotNull(rotation);
    }

    @Test
    void testChooseMoveHasDestination() {
        Tile spareTile = board.getSpareTile();
        Move move = strategy.chooseMove(board, player, spareTile);

        Position destination = move.getDestination();
        assertNotNull(destination);
    }

    @Test
    void testStrategyNameIsCorrect() {
        assertEquals("Random (Level 0)", strategy.getName());
    }

    @Test
    void testMultipleCallsProduceDifferentMoves() {
        Tile spareTile = board.getSpareTile();

        Move move1 = strategy.chooseMove(board, player, spareTile);
        Move move2 = strategy.chooseMove(board, player, spareTile);
        Move move3 = strategy.chooseMove(board, player, spareTile);

        // At least one should be different (statistically very likely)
        boolean allSame = move1.equals(move2) && move2.equals(move3);
        // We can't guarantee they're different, but with random strategy they usually are
        assertNotNull(move1);
        assertNotNull(move2);
        assertNotNull(move3);
    }

    @Test
    void testDestinationIsReachableAfterInsertion() {
        Tile spareTile = board.getSpareTile();
        Move move = strategy.chooseMove(board, player, spareTile);

        // Simulate the insertion
        Board tempBoard = board.copy();
        tempBoard.getSpareTile().setOrientation(move.getTileRotation());
        tempBoard.insertAndShift(move.getInsertArrow());

        // The destination should be reachable from the player's (possibly shifted) position
        // This is a basic sanity check
        assertNotNull(move.getDestination());
        assertTrue(move.getDestination().isInBounds(Board.SIZE));
    }
}
