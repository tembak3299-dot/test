package g65058.dev3.labyrinthe.view.javafx;

import g65058.dev3.labyrinthe.controller.GameController;
import g65058.dev3.labyrinthe.model.game.LabyrinthFacade;
import g65058.dev3.labyrinthe.model.observer.Observer;
import g65058.dev3.labyrinthe.view.javafx.ui.*;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

/**
 * Root pane for the JavaFX application.
 * Organizes the main UI components using BorderPane layout.
 */
public class RootPane extends BorderPane implements Observer {
    private final LabyrinthFacade facade;
    private final GameController controller;

    private final MenuPane menuPane;
    private final BoardPane boardPane;
    private final InfoPane infoPane;
    private final SpareTilePane spareTilePane;

    /**
     * Creates the root pane.
     *
     * @param facade     the game facade
     * @param controller the game controller
     */
    public RootPane(LabyrinthFacade facade, GameController controller) {
        this.facade = facade;
        this.controller = controller;

        // Create UI components
        this.menuPane = new MenuPane(controller);
        this.boardPane = new BoardPane(facade, controller);
        this.infoPane = new InfoPane(facade);
        this.spareTilePane = new SpareTilePane(facade, controller);

        // Layout
        setTop(menuPane);
        setCenter(boardPane);
        setRight(infoPane);
        setBottom(spareTilePane);

        setPadding(new Insets(10));
        setStyle("-fx-background-color: #2c3e50;");
    }

    @Override
    public void update() {
        // Propagate update to all child panes
        boardPane.update();
        infoPane.update();
        spareTilePane.update();
        menuPane.update();
    }
}
