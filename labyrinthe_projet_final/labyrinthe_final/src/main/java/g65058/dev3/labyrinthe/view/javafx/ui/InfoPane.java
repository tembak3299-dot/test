package g65058.dev3.labyrinthe.view.javafx.ui;

import g65058.dev3.labyrinthe.model.game.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.HBox;

/**
 * Information panel showing player status and current objective.
 */
public class InfoPane extends VBox {
    private final LabyrinthFacade facade;

    private final Label titleLabel;
    private final VBox playersBox;
    private final Label currentObjectiveLabel;
    private final Label phaseLabel;

    /**
     * Creates the info pane.
     *
     * @param facade the game facade
     */
    public InfoPane(LabyrinthFacade facade) {
        this.facade = facade;

        setSpacing(15);
        setPadding(new Insets(15));
        setPrefWidth(200);
        setStyle("-fx-background-color: #34495e; -fx-background-radius: 10;");

        // Title
        titleLabel = new Label("Players");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Players list
        playersBox = new VBox(10);

        // Current objective
        Label objectiveTitle = new Label("Current Objective:");
        objectiveTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        currentObjectiveLabel = new Label("-");
        currentObjectiveLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f1c40f;");
        currentObjectiveLabel.setWrapText(true);

        // Phase indicator
        phaseLabel = new Label("");
        phaseLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #bdc3c7;");

        getChildren().addAll(
                titleLabel,
                playersBox,
                new Rectangle(180, 2, Color.gray(0.5)),
                objectiveTitle,
                currentObjectiveLabel,
                phaseLabel
        );
    }

    /**
     * Updates the information display.
     */
    public void update() {
        playersBox.getChildren().clear();

        Player[] players = facade.getPlayers();
        Player currentPlayer = facade.getCurrentPlayer();

        for (Player player : players) {
            HBox playerRow = createPlayerRow(player, player == currentPlayer);
            playersBox.getChildren().add(playerRow);
        }

        // Update current objective
        if (currentPlayer != null) {
            ObjectiveCard card = currentPlayer.getCurrentObjective();
            if (card != null) {
                currentObjectiveLabel.setText(card.getObjective().toString());
            } else {
                currentObjectiveLabel.setText("Return to start!");
            }

            // Update phase
            GameState state = facade.getState();
            String phaseText = switch (state) {
                case WAITING_INSERT -> "Phase: Insert tile";
                case WAITING_MOVE -> "Phase: Move player";
                case FINISHED -> "Game Over!";
                case ABORTED -> "Game Abandoned";
                default -> "";
            };
            phaseLabel.setText(phaseText);
        }
    }

    /**
     * Creates a row for displaying player information.
     */
    private HBox createPlayerRow(Player player, boolean isCurrent) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));

        if (isCurrent) {
            row.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 5;");
        }

        // Color indicator
        Circle colorDot = new Circle(8);
        colorDot.setFill(getPlayerColor(player.getColor()));
        colorDot.setStroke(Color.WHITE);

        // Name and info
        VBox info = new VBox(2);
        Label nameLabel = new Label(player.getName());
        nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

        Label objectivesLabel = new Label("Objectives: " + player.getRemainingObjectives());
        objectivesLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6;");

        // Robot indicator
        if (player.isRobot()) {
            nameLabel.setText(player.getName() + " 🤖");
        }

        info.getChildren().addAll(nameLabel, objectivesLabel);

        // Current player indicator
        Label currentIndicator = new Label(isCurrent ? "◀" : "");
        currentIndicator.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 14px;");

        row.getChildren().addAll(colorDot, info, currentIndicator);
        return row;
    }

    /**
     * Converts PlayerColor to JavaFX Color.
     */
    private Color getPlayerColor(PlayerColor color) {
        return switch (color) {
            case YELLOW -> Color.YELLOW;
            case BLUE -> Color.DODGERBLUE;
            case GREEN -> Color.LIMEGREEN;
            case RED -> Color.TOMATO;
        };
    }
}
