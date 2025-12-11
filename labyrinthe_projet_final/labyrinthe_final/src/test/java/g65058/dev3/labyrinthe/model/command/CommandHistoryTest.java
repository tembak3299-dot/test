package g65058.dev3.labyrinthe.model.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CommandHistory class (Invoker).
 */
class CommandHistoryTest {
    private CommandHistory history;
    private int executeCount;
    private int undoCount;

    @BeforeEach
    void setUp() {
        history = new CommandHistory();
        executeCount = 0;
        undoCount = 0;
    }

    private Command createTestCommand() {
        return new Command() {
            @Override
            public void execute() {
                executeCount++;
            }

            @Override
            public void undo() {
                undoCount++;
            }
        };
    }

    @Test
    void testInitiallyEmpty() {
        assertFalse(history.canUndo());
        assertFalse(history.canRedo());
        assertEquals(0, history.getUndoCount());
        assertEquals(0, history.getRedoCount());
    }

    @Test
    void testExecuteCommand() {
        Command cmd = createTestCommand();
        history.executeCommand(cmd);

        assertEquals(1, executeCount);
        assertTrue(history.canUndo());
        assertFalse(history.canRedo());
    }

    @Test
    void testUndo() {
        Command cmd = createTestCommand();
        history.executeCommand(cmd);

        boolean result = history.undo();

        assertTrue(result);
        assertEquals(1, undoCount);
        assertFalse(history.canUndo());
        assertTrue(history.canRedo());
    }

    @Test
    void testRedo() {
        Command cmd = createTestCommand();
        history.executeCommand(cmd);
        history.undo();

        boolean result = history.redo();

        assertTrue(result);
        assertEquals(2, executeCount); // Execute called again
        assertTrue(history.canUndo());
        assertFalse(history.canRedo());
    }

    @Test
    void testUndoWhenEmpty() {
        boolean result = history.undo();
        assertFalse(result);
    }

    @Test
    void testRedoWhenEmpty() {
        boolean result = history.redo();
        assertFalse(result);
    }

    @Test
    void testNewCommandClearsRedo() {
        Command cmd1 = createTestCommand();
        Command cmd2 = createTestCommand();

        history.executeCommand(cmd1);
        history.undo();
        assertTrue(history.canRedo());

        history.executeCommand(cmd2);
        assertFalse(history.canRedo());
    }

    @Test
    void testMultipleUndoRedo() {
        for (int i = 0; i < 5; i++) {
            history.executeCommand(createTestCommand());
        }

        assertEquals(5, history.getUndoCount());

        for (int i = 0; i < 3; i++) {
            history.undo();
        }

        assertEquals(2, history.getUndoCount());
        assertEquals(3, history.getRedoCount());

        for (int i = 0; i < 2; i++) {
            history.redo();
        }

        assertEquals(4, history.getUndoCount());
        assertEquals(1, history.getRedoCount());
    }

    @Test
    void testClear() {
        history.executeCommand(createTestCommand());
        history.executeCommand(createTestCommand());
        history.undo();

        history.clear();

        assertFalse(history.canUndo());
        assertFalse(history.canRedo());
        assertEquals(0, history.getUndoCount());
        assertEquals(0, history.getRedoCount());
    }
}
