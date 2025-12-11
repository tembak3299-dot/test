package g65058.dev3.labyrinthe.view.javafx;

import g65058.dev3.labyrinthe.controller.GameController;
import g65058.dev3.labyrinthe.model.game.LabyrinthFacade;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX application for the Labyrinth game.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create model (facade)
        LabyrinthFacade facade = new LabyrinthFacade();

        // Create controller
        GameController controller = new GameController(facade);

        // Create root pane (view)
        RootPane rootPane = new RootPane(facade, controller);

        // Register view as observer
        facade.addObserver(rootPane);

        // Setup scene
        Scene scene = new Scene(rootPane, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/styles.css") != null ?
                getClass().getResource("/styles.css").toExternalForm() : "");

        primaryStage.setTitle("Labyrinthe - Chasse au trésor");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
