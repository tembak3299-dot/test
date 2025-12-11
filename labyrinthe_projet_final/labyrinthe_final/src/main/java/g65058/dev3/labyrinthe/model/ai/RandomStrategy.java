package g65058.dev3.labyrinthe.model.ai;

import g65058.dev3.labyrinthe.model.board.*;
import g65058.dev3.labyrinthe.model.game.Move;
import g65058.dev3.labyrinthe.model.game.Player;

import java.util.List;
import java.util.Random;

/**
 * Random strategy: chooses moves randomly (level 0).
 */
public class RandomStrategy implements Strategy {
    private final Random random;

    /**
     * Creates a new random strategy.
     */
    public RandomStrategy() {
        this.random = new Random();
    }

    /**
     * Creates a random strategy with a specific seed (for testing).
     *
     * @param seed the random seed
     */
    public RandomStrategy(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public Move chooseMove(Board board, Player player, Tile spareTile) {
        // Choose random valid arrow
        List<Arrow> validArrows = board.getValidArrows();
        Arrow chosenArrow = validArrows.get(random.nextInt(validArrows.size()));

        // Choose random rotation
        Direction[] directions = Direction.values();
        Direction rotation = directions[random.nextInt(directions.length)];

        // Simulate the insertion to find reachable positions
        Board tempBoard = board.copy();
        tempBoard.getSpareTile().setOrientation(rotation);
        tempBoard.insertAndShift(chosenArrow);

        // Get reachable positions after insertion
        Position currentPos = player.getPosition();

        // Adjust position if player was on the shifted row/column
        Position adjustedPos = adjustPositionAfterShift(currentPos, chosenArrow, board.getSize());

        List<Position> reachable = tempBoard.getReachablePositions(adjustedPos);

        // Choose random destination
        Position destination = reachable.get(random.nextInt(reachable.size()));

        return new Move(chosenArrow, rotation, destination);
    }

    /**
     * Adjusts player position after a tile shift.
     */
    private Position adjustPositionAfterShift(Position pos, Arrow arrow, int size) {
        int row = pos.getRow();
        int col = pos.getCol();
        Direction dir = arrow.getInsertDirection();

        if (arrow.isHorizontal() && row == arrow.getRow()) {
            // Horizontal shift affecting this row
            if (dir == Direction.EAST) {
                col = (col + 1) % size;
                if (col == 0) col = size - 1; // Wrapped around
            } else { // WEST
                col = (col - 1 + size) % size;
                if (col == size - 1) col = 0; // Wrapped around
            }
        } else if (!arrow.isHorizontal() && col == arrow.getCol()) {
            // Vertical shift affecting this column
            if (dir == Direction.SOUTH) {
                row = (row + 1) % size;
                if (row == 0) row = size - 1;
            } else { // NORTH
                row = (row - 1 + size) % size;
                if (row == size - 1) row = 0;
            }
        }

        return new Position(row, col);
    }

    @Override
    public String getName() {
        return "Random (Level 0)";
    }
}
