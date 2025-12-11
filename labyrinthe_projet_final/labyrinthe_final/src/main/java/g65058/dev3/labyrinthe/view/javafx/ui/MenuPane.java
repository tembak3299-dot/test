package g65058.dev3.labyrinthe.view.javafx.ui;

import g65058.dev3.labyrinthe.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * Menu bar pane containing game controls.
 * Handles new game, undo/redo, and abandon actions.
 */
public class MenuPane extends HBox {
    private final GameController controller;

    private final Button newGameButton;
    private final Button undoButton;
    private final Button redoButton;
    private final Button abandonButton;
    private final ComboBox<String> difficultyCombo;
    private final Label statusLabel;

    /**
     * Creates the menu pane.
     *
     * @param controller the game controller
     */
    public MenuPane(GameController controller) {
        this.controller = controller;

        // New game button
        newGameButton = new Button("New Game");
        newGameButton.setOnAction(e -> showNewGameDialog());

        // Difficulty selector
        difficultyCombo = new ComboBox<>();
        difficultyCombo.getItems().addAll("Easy (Random)", "Medium", "Hard");
        difficultyCombo.setValue("Easy (Random)");

        // Undo/Redo buttons
        undoButton = new Button("↶ Undo");
        undoButton.setOnAction(e -> controller.undo());
        undoButton.setDisable(true);

        redoButton = new Button("↷ Redo");
        redoButton.setOnAction(e -> controller.redo());
        redoButton.setDisable(true);

        // Abandon button
        abandonButton = new Button("Abandon");
        abandonButton.setOnAction(e -> confirmAbandon());
        abandonButton.setDisable(true);

        // Status label
        statusLabel = new Label("Click 'New Game' to start");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        // Layout
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #34495e; -fx-background-radius: 5;");

        getChildren().addAll(
                newGameButton,
                new Label("Difficulty:") {{
                    setStyle("-fx-text-fill: white;");
                }},
                difficultyCombo,
                new Separator() {{
                    setOrientation(javafx.geometry.Orientation.VERTICAL);
                }},
                undoButton,
                redoButton,
                new Separator() {{
                    setOrientation(javafx.geometry.Orientation.VERTICAL);
                }},
                abandonButton,
                new Separator() {{
                    setOrientation(javafx.geometry.Orientation.VERTICAL);
                }},
                statusLabel
        );
    }

    /**
     * Shows the new game dialog.
     */
    private void showNewGameDialog() {
        int difficulty = difficultyCombo.getSelectionModel().getSelectedIndex();
        controller.startNewGame(1, difficulty, true);
    }

    /**
     * Shows confirmation dialog for abandoning.
     */
    private void confirmAbandon() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Abandon Game");
        alert.setHeaderText("Are you sure you want to abandon the game?");
        alert.setContentText("This cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.abandon();
            }
        });
    }

    /**
     * Updates the menu state based on game state.
     */
    public void update() {
        boolean gameRunning = controller.isGameRunning();

        undoButton.setDisable(!controller.canUndo());
        redoButton.setDisable(!controller.canRedo());
        abandonButton.setDisable(!gameRunning);

        // Update status
        if (controller.isFinished()) {
            var winner = controller.getWinner();
            if (winner != null) {
                statusLabel.setText("Game Over! Winner: " + winner.getName());
            } else {
                statusLabel.setText("Game abandoned");
            }
        } else if (gameRunning) {
            var player = controller.getCurrentPlayer();
            if (player != null) {
                String phase = controller.isInsertPhase() ? "Insert tile" : "Move player";
                statusLabel.setText(player.getName() + "'s turn - " + phase);
            }
        }
    }
}
