package g65058.dev3.labyrinthe.view.javafx.ui;

import g65058.dev3.labyrinthe.controller.GameController;
import g65058.dev3.labyrinthe.model.board.Tile;
import g65058.dev3.labyrinthe.model.game.GameState;
import g65058.dev3.labyrinthe.model.game.LabyrinthFacade;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Pane displaying the spare tile with rotation controls.
 */
public class SpareTilePane extends HBox {
    private static final int TILE_SIZE = 80;

    private final LabyrinthFacade facade;
    private final GameController controller;

    private final StackPane tileDisplay;
    private final Rectangle background;
    private final ImageView imageView;
    private final Label tileInfoLabel;
    private final Button rotateButton;

    /**
     * Creates the spare tile pane.
     *
     * @param facade     the game facade
     * @param controller the game controller
     */
    public SpareTilePane(LabyrinthFacade facade, GameController controller) {
        this.facade = facade;
        this.controller = controller;

        setSpacing(20);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #34495e; -fx-background-radius: 10;");

        // Label
        Label titleLabel = new Label("Spare Tile:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Tile display
        tileDisplay = new StackPane();
        tileDisplay.setPrefSize(TILE_SIZE, TILE_SIZE);

        background = new Rectangle(TILE_SIZE, TILE_SIZE);
        background.setFill(Color.BURLYWOOD);
        background.setArcWidth(8);
        background.setArcHeight(8);
        background.setStroke(Color.GOLD);
        background.setStrokeWidth(3);

        imageView = new ImageView();
        imageView.setFitWidth(TILE_SIZE - 8);
        imageView.setFitHeight(TILE_SIZE - 8);
        imageView.setPreserveRatio(true);

        tileDisplay.getChildren().addAll(background, imageView);

        // Tile info
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        tileInfoLabel = new Label("-");
        tileInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

        // Rotate button
        rotateButton = new Button("↻ Rotate");
        rotateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        rotateButton.setOnAction(e -> {
            controller.rotateSpareTile();
            update();
        });

        infoBox.getChildren().addAll(tileInfoLabel, rotateButton);

        // Instructions
        Label instructionLabel = new Label("Click an arrow to insert the tile");
        instructionLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");

        getChildren().addAll(titleLabel, tileDisplay, infoBox, instructionLabel);
    }

    /**
     * Updates the spare tile display.
     */
    public void update() {
        Tile spare = facade.getSpareTile();

        if (spare == null) {
            tileInfoLabel.setText("-");
            background.setFill(Color.GRAY);
            rotateButton.setDisable(true);
            return;
        }

        // Update info label
        String info = spare.getType() + " - " + spare.getOrientation();
        if (spare.hasObjective()) {
            info += "\n" + spare.getObjective();
        }
        tileInfoLabel.setText(info);

        // Update visual
        Color bgColor = switch (spare.getType()) {
            case STRAIGHT -> Color.SANDYBROWN;
            case CORNER -> Color.BURLYWOOD;
            case T_JUNCTION -> Color.TAN;
        };
        background.setFill(bgColor);

        // Rotate image to match orientation
        double angle = switch (spare.getOrientation()) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
        };
        imageView.setRotate(angle);

        // Enable/disable rotation
        boolean canRotate = facade.getState() == GameState.WAITING_INSERT;
        rotateButton.setDisable(!canRotate);
    }
}
