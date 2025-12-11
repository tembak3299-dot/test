package g65058.dev3.labyrinthe.model.board;

/**
 * Represents the 24 different objectives in the game.
 * Each objective has an associated image filename.
 */
public enum Objective {
    // Mobile tiles objectives (L-shaped with objectives: 6 pieces)
    GHOST("goal_ghost.jpg"),
    DRAGON("goal_dragon.jpg"),
    BAT("goal_bat.jpg"),
    OWL("goal_hibou.jpg"),
    SPIDER("goal_spider.jpg"),
    LIZARD("goal_lezard.jpg"),

    // Mobile T-shaped tiles with objectives (6 pieces)
    GNOME("goal_pig.jpg"),          // gnome/pig
    FAIRY("goal_witch.jpg"),        // fairy/witch
    GENIE("goal_ghost2.jpg"),       // genie
    SCARAB("goal_insecte.jpg"),     // scarab/insect
    RAT("goal_mouse.jpg"),          // rat/mouse
    BUTTERFLY("goal_butteryfly.jpg"),

    // Fixed T-junction tiles with objectives (12 pieces)
    GRIMOIRE("goal_book.jpg"),
    GOLD_BAG("goal_money.jpg"),
    MAP("goal_map.jpg"),
    CROWN("goal_crown.jpg"),
    KEYS("goal_keys.jpg"),
    BONES("goal_skull.jpg"),
    RING("goal_ring.jpg"),
    TREASURE_CHEST("goal_coffre.jpg"),
    EMERALD("goal_saphir.jpg"),
    SWORD("goal_sword.jpg"),
    CANDLE("goal_candleholder.jpg"),
    HELMET("goal_helmet.jpg");

    private final String imagePath;

    Objective(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return the image filename for this objective
     */
    public String getImagePath() {
        return imagePath;
    }
}
