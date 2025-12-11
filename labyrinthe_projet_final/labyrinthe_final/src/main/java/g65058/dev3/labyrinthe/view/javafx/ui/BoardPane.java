package g65058.dev3.labyrinthe.view.javafx.ui;

import g65058.dev3.labyrinthe.controller.GameController;
import g65058.dev3.labyrinthe.model.board.*;
import g65058.dev3.labyrinthe.model.game.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Pane displaying the game board with tiles and players.
 * Handles mouse interactions for tile insertion and player movement.
 */
public class BoardPane extends GridPane {
    private static final int TILE_SIZE = 70;
    private static final int BOARD_SIZE = 7;

    private final LabyrinthFacade facade;
    private final GameController controller;
    private final TilePane[][] tilePanes;
    private final ArrowButton[] arrowButtons;

    /**
     * Creates the board pane.
     *
     * @param facade     the game facade
     * @param controller the game controller
     */
    public BoardPane(LabyrinthFacade facade, GameController controller) {
        this.facade = facade;
        this.controller = controller;
        this.tilePanes = new TilePane[BOARD_SIZE][BOARD_SIZE];
        this.arrowButtons = new ArrowButton[12];

        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setHgap(2);
        setVgap(2);
        setStyle("-fx-background-color: #1a252f; -fx-background-radius: 10;");

        initializeBoard();
    }

    /**
     * Initializes the board grid with tiles and arrow buttons.
     */
    private void initializeBoard() {
        // Create arrow buttons and tile panes
        // Row 0: top arrows
        add(new Label(), 0, 0); // Corner
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (col % 2 == 1) {
                ArrowButton arrow = new ArrowButton(Direction.SOUTH, col);
                arrow.setOnAction(e -> handleArrowClick(arrow));
                add(arrow, col + 1, 0);
            } else {
                add(new Label(), col + 1, 0);
            }
        }
        add(new Label(), BOARD_SIZE + 1, 0); // Corner

        // Main grid rows
        for (int row = 0; row < BOARD_SIZE; row++) {
            // Left arrow
            if (row % 2 == 1) {
                ArrowButton arrow = new ArrowButton(Direction.EAST, row);
                arrow.setOnAction(e -> handleArrowClick(arrow));
                add(arrow, 0, row + 1);
            } else {
                add(new Label(), 0, row + 1);
            }

            // Tiles
            for (int col = 0; col < BOARD_SIZE; col++) {
                TilePane tilePane = new TilePane(row, col);
                tilePane.setOnMouseClicked(e -> handleTileClick(tilePane));
                tilePane.setOnMouseEntered(e -> handleTileHover(tilePane, true));
                tilePane.setOnMouseExited(e -> handleTileHover(tilePane, false));
                tilePanes[row][col] = tilePane;
                add(tilePane, col + 1, row + 1);
            }

            // Right arrow
            if (row % 2 == 1) {
                ArrowButton arrow = new ArrowButton(Direction.WEST, row);
                arrow.setOnAction(e -> handleArrowClick(arrow));
                add(arrow, BOARD_SIZE + 1, row + 1);
            } else {
                add(new Label(), BOARD_SIZE + 1, row + 1);
            }
        }

        // Bottom arrows
        add(new Label(), 0, BOARD_SIZE + 1); // Corner
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (col % 2 == 1) {
                ArrowButton arrow = new ArrowButton(Direction.NORTH, col);
                arrow.setOnAction(e -> handleArrowClick(arrow));
                add(arrow, col + 1, BOARD_SIZE + 1);
            } else {
                add(new Label(), col + 1, BOARD_SIZE + 1);
            }
        }
        add(new Label(), BOARD_SIZE + 1, BOARD_SIZE + 1); // Corner
    }

    /**
     * Handles arrow button clicks for tile insertion.
     */
    private void handleArrowClick(ArrowButton arrow) {
        if (facade.getState() != GameState.WAITING_INSERT) {
            return;
        }

        Arrow gameArrow = arrow.toArrow();
        if (facade.canInsert(gameArrow)) {
            controller.insertTile(gameArrow);
        }
    }

    /**
     * Handles tile clicks for player movement.
     */
    private void handleTileClick(TilePane tilePane) {
        if (facade.getState() != GameState.WAITING_MOVE) {
            return;
        }

        Position destination = new Position(tilePane.getBoardRow(), tilePane.getBoardCol());
        if (facade.canMove(destination)) {
            controller.movePlayer(destination);
        }
    }

    /**
     * Handles tile hover for visual feedback.
     */
    private void handleTileHover(TilePane tilePane, boolean entering) {
        GameState state = facade.getState();

        if (state == GameState.WAITING_MOVE) {
            Position pos = new Position(tilePane.getBoardRow(), tilePane.getBoardCol());
            boolean canMove = facade.canMove(pos);

            if (entering) {
                tilePane.setHighlight(canMove ? Color.LIGHTGREEN : Color.LIGHTCORAL);
            } else {
                tilePane.clearHighlight();
            }
        }
    }

    /**
     * Updates the board display.
     */
    public void update() {
        Board board = facade.getBoard();
        if (board == null) {
            return;
        }

        // Update all tiles
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Tile tile = board.getTile(row, col);
                tilePanes[row][col].updateTile(tile);
            }
        }

        // Update player positions
        clearPlayers();
        for (Player player : facade.getPlayers()) {
            Position pos = player.getPosition();
            tilePanes[pos.getRow()][pos.getCol()].addPlayer(player);
        }

        // Highlight reachable positions in move phase
        if (facade.getState() == GameState.WAITING_MOVE) {
            List<Position> reachable = facade.getReachablePositions();
            for (Position pos : reachable) {
                tilePanes[pos.getRow()][pos.getCol()].setReachable(true);
            }
        }
    }

    /**
     * Clears all player markers from tiles.
     */
    private void clearPlayers() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                tilePanes[row][col].clearPlayers();
                tilePanes[row][col].setReachable(false);
            }
        }
    }

    /**
     * Inner class for tile display.
     */
    private static class TilePane extends StackPane {
        private final int boardRow;
        private final int boardCol;
        private final Rectangle background;
        private final ImageView imageView;
        private final HBox playerBox;

        TilePane(int row, int col) {
            this.boardRow = row;
            this.boardCol = col;

            setPrefSize(TILE_SIZE, TILE_SIZE);
            setMinSize(TILE_SIZE, TILE_SIZE);
            setMaxSize(TILE_SIZE, TILE_SIZE);

            background = new Rectangle(TILE_SIZE, TILE_SIZE);
            background.setFill(Color.BURLYWOOD);
            background.setArcWidth(5);
            background.setArcHeight(5);

            imageView = new ImageView();
            imageView.setFitWidth(TILE_SIZE - 4);
            imageView.setFitHeight(TILE_SIZE - 4);
            imageView.setPreserveRatio(true);

            playerBox = new HBox(2);
            playerBox.setAlignment(Pos.CENTER);

            getChildren().addAll(background, imageView, playerBox);
        }

        int getBoardRow() {
            return boardRow;
        }

        int getBoardCol() {
            return boardCol;
        }

        void updateTile(Tile tile) {
            if (tile == null) {
                background.setFill(Color.GRAY);
                imageView.setImage(null);
                return;
            }

            // Set background color based on tile type
            Color bgColor = switch (tile.getType()) {
                case STRAIGHT -> Color.SANDYBROWN;
                case CORNER -> Color.BURLYWOOD;
                case T_JUNCTION -> Color.TAN;
            };
            background.setFill(bgColor);

            // Try to load tile image
            String imagePath = getTileImagePath(tile);
            try {
                var resourceStream = getClass().getResourceAsStream(imagePath);
                if (resourceStream == null) {
                    System.err.println("Image not found: " + imagePath);
                    imageView.setImage(null);
                } else {
                    Image image = new Image(resourceStream);
                    imageView.setImage(image);
                    // Rotate based on orientation
                    imageView.setRotate(getRotationAngle(tile.getOrientation()));
                }
            } catch (Exception e) {
                // Fallback: no image, just show background color
                System.err.println("Error loading image " + imagePath + ": " + e.getMessage());
                imageView.setImage(null);
            }

            // Show objective indicator
            if (tile.hasObjective()) {
                background.setStroke(Color.GOLD);
                background.setStrokeWidth(3);
            } else {
                background.setStroke(null);
            }
        }

        private String getTileImagePath(Tile tile) {
            return switch (tile.getType()) {
                case STRAIGHT -> "/images/mobile_tiles/I_Shape.jpg";
                case CORNER -> {
                    if (tile.isFixed()) {
                        // Fixed corner tiles use specific corner images based on orientation
                        yield getFixedCornerImagePath(tile.getOrientation());
                    } else if (tile.hasObjective()) {
                        // Mobile L-shaped tiles with objectives are in root images folder
                        yield "/images/" + tile.getObjective().getImagePath();
                    } else {
                        yield "/images/mobile_tiles/L_tile.jpg";
                    }
                }
                case T_JUNCTION -> {
                    if (tile.hasObjective()) {
                        Objective obj = tile.getObjective();
                        // Fixed objectives are in fixed_tiles folder, mobile objectives in root
                        if (isFixedObjective(obj)) {
                            yield "/images/fixed_tiles/" + obj.getImagePath();
                        } else {
                            yield "/images/" + obj.getImagePath();
                        }
                    } else {
                        yield "/images/mobile_tiles/L_tile.jpg";
                    }
                }
            };
        }

        private String getFixedCornerImagePath(Direction orientation) {
            return switch (orientation) {
                case SOUTH -> "/images/corners/fixed_tile_upleft_corner.jpg";
                case WEST -> "/images/corners/fixed_tile_upright_corner.jpg";
                case NORTH -> "/images/corners/fixed_tile_downright_corner.jpg";
                case EAST -> "/images/corners/fixed_tile_downleft_corner.jpg";
            };
        }

        private boolean isFixedObjective(Objective obj) {
            return switch (obj) {
                case GRIMOIRE, GOLD_BAG, MAP, CROWN, KEYS, BONES,
                     RING, TREASURE_CHEST, EMERALD, SWORD, CANDLE, HELMET -> true;
                default -> false;
            };
        }

        private double getRotationAngle(Direction orientation) {
            return switch (orientation) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
            };
        }

        void addPlayer(Player player) {
            Circle marker = new Circle(8);
            marker.setFill(getPlayerColor(player.getColor()));
            marker.setStroke(Color.WHITE);
            marker.setStrokeWidth(2);
            playerBox.getChildren().add(marker);
        }

        private Color getPlayerColor(PlayerColor color) {
            return switch (color) {
                case YELLOW -> Color.YELLOW;
                case BLUE -> Color.BLUE;
                case GREEN -> Color.GREEN;
                case RED -> Color.RED;
            };
        }

        void clearPlayers() {
            playerBox.getChildren().clear();
        }

        void setHighlight(Color color) {
            background.setStroke(color);
            background.setStrokeWidth(3);
        }

        void clearHighlight() {
            background.setStroke(null);
        }

        void setReachable(boolean reachable) {
            if (reachable) {
                setOpacity(1.0);
            } else {
                setOpacity(0.8);
            }
        }
    }

    /**
     * Inner class for arrow buttons.
     */
    private static class ArrowButton extends javafx.scene.control.Button {
        private final Direction direction;
        private final int index;

        ArrowButton(Direction direction, int index) {
            this.direction = direction;
            this.index = index;

            String arrow = switch (direction) {
                case NORTH -> "↑";
                case SOUTH -> "↓";
                case EAST -> "→";
                case WEST -> "←";
            };

            setText(arrow);
            setPrefSize(30, 30);
            setStyle("-fx-font-size: 16px; -fx-background-color: #3498db; -fx-text-fill: white;");

            setOnMouseEntered(e -> setStyle("-fx-font-size: 16px; -fx-background-color: #2980b9; -fx-text-fill: white;"));
            setOnMouseExited(e -> setStyle("-fx-font-size: 16px; -fx-background-color: #3498db; -fx-text-fill: white;"));
        }

        Arrow toArrow() {
            int row, col;
            switch (direction) {
                case SOUTH -> {
                    row = 0;
                    col = index;
                }
                case NORTH -> {
                    row = 6;
                    col = index;
                }
                case EAST -> {
                    row = index;
                    col = 0;
                }
                case WEST -> {
                    row = index;
                    col = 6;
                }
                default -> throw new IllegalStateException();
            }
            return new Arrow(new Position(row, col), direction);
        }
    }
}
