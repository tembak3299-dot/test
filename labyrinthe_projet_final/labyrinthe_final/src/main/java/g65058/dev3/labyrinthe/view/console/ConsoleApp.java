package g65058.dev3.labyrinthe.view.console;

import g65058.dev3.labyrinthe.model.board.*;
import g65058.dev3.labyrinthe.model.game.*;

import java.util.List;
import java.util.Scanner;

/**
 * Console application for playing the Labyrinth game.
 * Provides a text-based interface for testing the model.
 */
public class ConsoleApp {
    private final LabyrinthFacade facade;
    private final Scanner scanner;

    /**
     * Creates the console application.
     */
    public ConsoleApp() {
        this.facade = new LabyrinthFacade();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp();
        app.run();
    }

    /**
     * Runs the game loop.
     */
    public void run() {
        System.out.println("=== LABYRINTH ===");
        System.out.println("Welcome to the Labyrinth game!");

        // Start a new game (1 human, 3 AI, simplified version)
        facade.startNewGame(1, 0, true);

        // Game loop
        while (!facade.isFinished()) {
            displayBoard();
            displayCurrentPlayer();

            if (facade.isCurrentPlayerRobot()) {
                System.out.println("AI is playing...");
                facade.playAITurn();
                pause(500);
            } else {
                playHumanTurn();
            }
        }

        // Game over
        displayBoard();
        Player winner = facade.getWinner();
        if (winner != null) {
            System.out.println("\n*** GAME OVER ***");
            System.out.println("Winner: " + winner.getName() + " (" + winner.getColor() + ")");
        } else {
            System.out.println("\nGame was abandoned.");
        }
    }

    /**
     * Displays the game board.
     */
    private void displayBoard() {
        Board board = facade.getBoard();
        System.out.println("\n  0 1 2 3 4 5 6");
        System.out.println("  -------------");

        for (int row = 0; row < Board.SIZE; row++) {
            System.out.print(row + "|");
            for (int col = 0; col < Board.SIZE; col++) {
                Tile tile = board.getTile(row, col);
                System.out.print(getTileSymbol(tile) + " ");
            }
            System.out.println("|");
        }
        System.out.println("  -------------");

        // Display spare tile
        Tile spare = facade.getSpareTile();
        System.out.println("Spare tile: " + getTileSymbol(spare) + " " + spare);

        // Display players
        System.out.println("\nPlayers:");
        for (Player player : facade.getPlayers()) {
            String marker = player == facade.getCurrentPlayer() ? " <--" : "";
            System.out.println("  " + player.getColor() + " at " + player.getPosition() +
                    " (Objectives: " + player.getRemainingObjectives() + ")" + marker);
        }
    }

    /**
     * Returns a character symbol for a tile type.
     */
    private String getTileSymbol(Tile tile) {
        return switch (tile.getType()) {
            case STRAIGHT -> "I";
            case CORNER -> "L";
            case T_JUNCTION -> "T";
        };
    }

    /**
     * Displays information about the current player.
     */
    private void displayCurrentPlayer() {
        Player player = facade.getCurrentPlayer();
        System.out.println("\n--- " + player.getName() + "'s turn (" + player.getColor() + ") ---");

        ObjectiveCard objective = player.getCurrentObjective();
        if (objective != null) {
            System.out.println("Current objective: " + objective.getObjective());
        } else {
            System.out.println("All objectives complete! Return to start position.");
        }
    }

    /**
     * Handles a human player's turn.
     */
    private void playHumanTurn() {
        // Phase 1: Insert tile
        System.out.println("\n[Phase 1: Insert tile]");
        System.out.println("Available arrows:");

        List<Arrow> arrows = facade.getValidArrows();
        for (int i = 0; i < arrows.size(); i++) {
            Arrow arrow = arrows.get(i);
            System.out.println("  " + i + ": " + arrow);
        }

        System.out.println("Enter 'r' to rotate spare tile, or arrow number to insert:");
        String input = scanner.nextLine().trim().toLowerCase();

        while (input.equals("r")) {
            facade.rotateSpareTile();
            System.out.println("Spare tile rotated: " + facade.getSpareTile());
            input = scanner.nextLine().trim().toLowerCase();
        }

        int arrowIndex = Integer.parseInt(input);
        Arrow selectedArrow = arrows.get(arrowIndex);

        // Insert the tile
        facade.insertTile(selectedArrow);

        // Display updated board
        displayBoard();

        // Phase 2: Move player
        System.out.println("\n[Phase 2: Move player]");
        List<Position> reachable = facade.getReachablePositions();
        System.out.println("Reachable positions: " + reachable);

        System.out.print("Enter destination (row col) or 's' to stay: ");
        input = scanner.nextLine().trim().toLowerCase();

        Position destination;
        if (input.equals("s")) {
            destination = facade.getCurrentPlayer().getPosition();
        } else {
            String[] parts = input.split("\\s+");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            destination = new Position(row, col);
        }

        facade.movePlayer(destination);
    }

    /**
     * Pauses execution for the specified milliseconds.
     */
    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
