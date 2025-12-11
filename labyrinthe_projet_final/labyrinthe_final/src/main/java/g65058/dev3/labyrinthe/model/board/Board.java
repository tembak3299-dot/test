package g65058.dev3.labyrinthe.model.board;

import java.util.*;

/**
 * Represents the 7x7 game board with corridor tiles.
 * Manages tile placement, insertion/shifting, and path finding.
 */
public class Board {
    public static final int SIZE = 7;
    private final Tile[][] tiles;
    private Tile spareTile;
    private Arrow lastArrow;
    private final List<Position> playerPositions;

    /**
     * Creates a new board and initializes it with tiles.
     */
    public Board() {
        this.tiles = new Tile[SIZE][SIZE];
        this.playerPositions = new ArrayList<>();
        initializeBoard();
    }

    /**
     * Creates a board from existing state (for undo/redo).
     */
    public Board(Tile[][] tiles, Tile spareTile, Arrow lastArrow) {
        this.tiles = new Tile[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                this.tiles[r][c] = tiles[r][c] != null ? tiles[r][c].copy() : null;
            }
        }
        this.spareTile = spareTile != null ? spareTile.copy() : null;
        this.lastArrow = lastArrow;
        this.playerPositions = new ArrayList<>();
    }

    /**
     * Initializes the board with fixed and mobile tiles according to game rules.
     */
    private void initializeBoard() {
        // Create objective lists
        List<Objective> fixedObjectives = new ArrayList<>(Arrays.asList(
                Objective.GRIMOIRE, Objective.GOLD_BAG, Objective.MAP, Objective.CROWN,
                Objective.KEYS, Objective.BONES, Objective.RING, Objective.TREASURE_CHEST,
                Objective.EMERALD, Objective.SWORD, Objective.CANDLE, Objective.HELMET
        ));
        Collections.shuffle(fixedObjectives);

        List<Objective> mobileTObjectives = new ArrayList<>(Arrays.asList(
                Objective.GNOME, Objective.FAIRY, Objective.GENIE,
                Objective.SCARAB, Objective.RAT, Objective.BUTTERFLY
        ));
        Collections.shuffle(mobileTObjectives);

        List<Objective> mobileLObjectives = new ArrayList<>(Arrays.asList(
                Objective.GHOST, Objective.DRAGON, Objective.BAT,
                Objective.OWL, Objective.SPIDER, Objective.LIZARD
        ));
        Collections.shuffle(mobileLObjectives);

        // Initialize fixed corner tiles (player starting positions)
        tiles[0][0] = new Tile(TileType.CORNER, Direction.SOUTH, null, true);  // Player 1
        tiles[0][6] = new Tile(TileType.CORNER, Direction.WEST, null, true);   // Player 2
        tiles[6][6] = new Tile(TileType.CORNER, Direction.NORTH, null, true);  // Player 3
        tiles[6][0] = new Tile(TileType.CORNER, Direction.EAST, null, true);   // Player 4

        // Initialize fixed T-junction tiles with objectives (12 tiles on even positions)
        int objIndex = 0;
        
        // Row 0: positions (0,2) and (0,4)
        tiles[0][2] = new Tile(TileType.T_JUNCTION, Direction.SOUTH, fixedObjectives.get(objIndex++), true);
        tiles[0][4] = new Tile(TileType.T_JUNCTION, Direction.SOUTH, fixedObjectives.get(objIndex++), true);

        // Row 2: positions (2,0), (2,2), (2,4), (2,6)
        tiles[2][0] = new Tile(TileType.T_JUNCTION, Direction.EAST, fixedObjectives.get(objIndex++), true);
        tiles[2][2] = new Tile(TileType.T_JUNCTION, Direction.SOUTH, fixedObjectives.get(objIndex++), true);
        tiles[2][4] = new Tile(TileType.T_JUNCTION, Direction.SOUTH, fixedObjectives.get(objIndex++), true);
        tiles[2][6] = new Tile(TileType.T_JUNCTION, Direction.WEST, fixedObjectives.get(objIndex++), true);

        // Row 4: positions (4,0), (4,2), (4,4), (4,6)
        tiles[4][0] = new Tile(TileType.T_JUNCTION, Direction.EAST, fixedObjectives.get(objIndex++), true);
        tiles[4][2] = new Tile(TileType.T_JUNCTION, Direction.NORTH, fixedObjectives.get(objIndex++), true);
        tiles[4][4] = new Tile(TileType.T_JUNCTION, Direction.NORTH, fixedObjectives.get(objIndex++), true);
        tiles[4][6] = new Tile(TileType.T_JUNCTION, Direction.WEST, fixedObjectives.get(objIndex++), true);

        // Row 6: positions (6,2) and (6,4)
        tiles[6][2] = new Tile(TileType.T_JUNCTION, Direction.NORTH, fixedObjectives.get(objIndex++), true);
        tiles[6][4] = new Tile(TileType.T_JUNCTION, Direction.NORTH, fixedObjectives.get(objIndex++), true);

        // Create mobile tiles pool: 12 I, 16 L (6 with objectives), 6 T (all with objectives)
        List<Tile> mobileTiles = new ArrayList<>();

        // 12 straight (I) tiles without objectives
        for (int i = 0; i < 12; i++) {
            Direction[] orientations = Direction.values();
            mobileTiles.add(new Tile(TileType.STRAIGHT, orientations[i % 2], null, false));
        }

        // 16 corner (L) tiles, 6 with objectives
        for (int i = 0; i < 16; i++) {
            Direction orientation = Direction.values()[i % 4];
            Objective obj = i < 6 ? mobileLObjectives.get(i) : null;
            mobileTiles.add(new Tile(TileType.CORNER, orientation, obj, false));
        }

        // 6 T-junction tiles with objectives
        for (int i = 0; i < 6; i++) {
            Direction orientation = Direction.values()[i % 4];
            mobileTiles.add(new Tile(TileType.T_JUNCTION, orientation, mobileTObjectives.get(i), false));
        }

        // Shuffle mobile tiles
        Collections.shuffle(mobileTiles);

        // Place mobile tiles on empty positions
        int tileIndex = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (tiles[r][c] == null) {
                    tiles[r][c] = mobileTiles.get(tileIndex++);
                }
            }
        }

        // The last tile becomes the spare tile
        spareTile = mobileTiles.get(tileIndex);
        lastArrow = null;
    }

    /**
     * @return the board size (7)
     */
    public int getSize() {
        return SIZE;
    }

    /**
     * Returns the tile at the given position.
     *
     * @param position the position
     * @return the tile at that position
     */
    public Tile getTile(Position position) {
        return tiles[position.getRow()][position.getCol()];
    }

    /**
     * Returns the tile at the given coordinates.
     *
     * @param row the row
     * @param col the column
     * @return the tile at that position
     */
    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    /**
     * @return the current spare tile
     */
    public Tile getSpareTile() {
        return spareTile;
    }

    /**
     * @return the last arrow used for insertion, or null if none
     */
    public Arrow getLastArrow() {
        return lastArrow;
    }

    /**
     * Registers a player position to update on tile shifts.
     *
     * @param position the player's position
     */
    public void registerPlayerPosition(Position position) {
        playerPositions.add(position);
    }

    /**
     * Checks if an insertion is valid (not the reverse of the last insertion).
     *
     * @param arrow the arrow to check
     * @return true if the insertion is allowed
     */
    public boolean canInsert(Arrow arrow) {
        if (lastArrow == null) {
            return true;
        }
        Arrow forbidden = lastArrow.getOpposite(SIZE);
        return !arrow.equals(forbidden);
    }

    /**
     * Inserts the spare tile at the given arrow position and shifts the row/column.
     *
     * @param arrow the insertion arrow
     * @return the expelled tile (becomes the new spare)
     * @throws IllegalArgumentException if the insertion reverses the last move
     */
    public Tile insertAndShift(Arrow arrow) {
        if (!canInsert(arrow)) {
            throw new IllegalArgumentException("Cannot reverse the previous insertion");
        }

        Tile expelled;
        Position insertPos = arrow.getPosition();
        Direction dir = arrow.getInsertDirection();
        int row = insertPos.getRow();
        int col = insertPos.getCol();

        switch (dir) {
            case SOUTH -> {
                // Insert from top, push down
                expelled = tiles[SIZE - 1][col];
                for (int r = SIZE - 1; r > 0; r--) {
                    tiles[r][col] = tiles[r - 1][col];
                }
                tiles[0][col] = spareTile;
                updatePlayerPositionsVertical(col, dir);
            }
            case NORTH -> {
                // Insert from bottom, push up
                expelled = tiles[0][col];
                for (int r = 0; r < SIZE - 1; r++) {
                    tiles[r][col] = tiles[r + 1][col];
                }
                tiles[SIZE - 1][col] = spareTile;
                updatePlayerPositionsVertical(col, dir);
            }
            case EAST -> {
                // Insert from left, push right
                expelled = tiles[row][SIZE - 1];
                for (int c = SIZE - 1; c > 0; c--) {
                    tiles[row][c] = tiles[row][c - 1];
                }
                tiles[row][0] = spareTile;
                updatePlayerPositionsHorizontal(row, dir);
            }
            case WEST -> {
                // Insert from right, push left
                expelled = tiles[row][0];
                for (int c = 0; c < SIZE - 1; c++) {
                    tiles[row][c] = tiles[row][c + 1];
                }
                tiles[row][SIZE - 1] = spareTile;
                updatePlayerPositionsHorizontal(row, dir);
            }
            default -> throw new IllegalStateException("Unknown direction: " + dir);
        }

        spareTile = expelled;
        lastArrow = arrow;
        return expelled;
    }

    /**
     * Updates player positions after vertical shift.
     */
    private void updatePlayerPositionsVertical(int col, Direction dir) {
        // This would be called by the Game class with actual player references
    }

    /**
     * Updates player positions after horizontal shift.
     */
    private void updatePlayerPositionsHorizontal(int row, Direction dir) {
        // This would be called by the Game class with actual player references
    }

    /**
     * Returns all positions reachable from the given starting position using BFS.
     *
     * @param start the starting position
     * @return list of reachable positions
     */
    public List<Position> getReachablePositions(Position start) {
        boolean[][] visited = new boolean[SIZE][SIZE];
        Queue<Position> queue = new ArrayDeque<>();
        List<Position> reachable = new ArrayList<>();

        visited[start.getRow()][start.getCol()] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            reachable.add(current);
            Tile currentTile = getTile(current);

            for (Direction dir : Direction.values()) {
                Position neighbor = current.move(dir);
                if (!neighbor.isInBounds(SIZE)) continue;
                if (visited[neighbor.getRow()][neighbor.getCol()]) continue;

                Tile neighborTile = getTile(neighbor);
                // Can move if current tile has opening towards neighbor
                // AND neighbor tile has opening towards current
                if (currentTile.isOpenTo(dir) && neighborTile.isOpenTo(dir.opposite())) {
                    visited[neighbor.getRow()][neighbor.getCol()] = true;
                    queue.add(neighbor);
                }
            }
        }

        return reachable;
    }

    /**
     * Checks if a position is reachable from another position.
     *
     * @param from the starting position
     * @param to   the target position
     * @return true if there is a path
     */
    public boolean isReachable(Position from, Position to) {
        return getReachablePositions(from).contains(to);
    }

    /**
     * Creates a deep copy of the board state.
     *
     * @return a copy of this board
     */
    public Board copy() {
        return new Board(tiles, spareTile, lastArrow);
    }

    /**
     * Returns all valid insertion arrows.
     *
     * @return list of all 12 arrows
     */
    public List<Arrow> getAllArrows() {
        return Arrow.getAllArrows();
    }

    /**
     * Returns insertion arrows that are currently allowed.
     *
     * @return list of valid arrows (excludes the forbidden reverse)
     */
    public List<Arrow> getValidArrows() {
        List<Arrow> valid = new ArrayList<>();
        for (Arrow arrow : getAllArrows()) {
            if (canInsert(arrow)) {
                valid.add(arrow);
            }
        }
        return valid;
    }
}
