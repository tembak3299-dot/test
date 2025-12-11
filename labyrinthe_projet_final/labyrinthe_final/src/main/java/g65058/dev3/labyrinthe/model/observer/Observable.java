package g65058.dev3.labyrinthe.model.observer;

/**
 * Observable interface for the Observer pattern.
 * The model implements this to notify views of changes.
 */
public interface Observable {
    /**
     * Adds an observer to be notified of changes.
     *
     * @param observer the observer to add
     */
    void addObserver(Observer observer);

    /**
     * Removes an observer.
     *
     * @param observer the observer to remove
     */
    void removeObserver(Observer observer);
}
