package g65058.dev3.labyrinthe.model.observer;

/**
 * Observer interface for the Observer pattern.
 * Views implement this to receive updates from the model.
 */
public interface Observer {
    /**
     * Called when the observed object has changed.
     */
    void update();
}
