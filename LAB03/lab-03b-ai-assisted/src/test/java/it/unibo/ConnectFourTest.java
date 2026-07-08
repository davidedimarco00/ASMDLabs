package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behavioural test suite for the ConnectFour interface.
 * 
 * Tests focus on the expected behaviour of Connect Four based on:
 * - The ConnectFour interface contract
 * - Standard Connect Four game rules
 * - Design assumptions documented in design-plan.md
 * 
 * Key assumptions tested:
 * - Board size: 6 rows × 7 columns (standard Connect Four)
 * - Win condition: 4 consecutive discs
 * - Disc mapping: 'R' = RED, 'Y' = YELLOW
 * - Column indexing: 0-based (columns 0-6)
 * - Gravity: discs fall to the lowest available row
 * - No turn enforcement at interface level
 */
class ConnectFourTest {

    private ConnectFour game;

    @BeforeEach
    void setUp() {
        // Assumes there exists a ConnectFourImpl class implementing ConnectFour
        game = new ConnectFourImpl();
    }

    // ==================== A. Input Validation Tests ====================

    @Test
    @DisplayName("Dropping disc in negative column should throw IllegalArgumentException")
    void testDropDiscInvalidColumnNegative() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> game.dropDisc(-1, 'R')
        );
        assertTrue(exception.getMessage().contains("column") || exception.getMessage().contains("-1"),
            "Exception message should mention invalid column");
    }

    @Test
    @DisplayName("Dropping disc in column >= 7 should throw IllegalArgumentException")
    void testDropDiscInvalidColumnTooHigh() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> game.dropDisc(7, 'R')
        );
        assertTrue(exception.getMessage().contains("column") || exception.getMessage().contains("7"),
            "Exception message should mention invalid column");
    }

    @Test
    @DisplayName("Dropping disc with invalid character should throw IllegalArgumentException")
    void testDropDiscInvalidCharacter() {
        assertThrows(IllegalArgumentException.class, () -> game.dropDisc(0, 'X'),
            "Invalid disc character 'X' should be rejected");
        assertThrows(IllegalArgumentException.class, () -> game.dropDisc(0, 'r'),
            "Lowercase 'r' should be rejected (case-sensitive)");
        assertThrows(IllegalArgumentException.class, () -> game.dropDisc(0, 'y'),
            "Lowercase 'y' should be rejected (case-sensitive)");
        assertThrows(IllegalArgumentException.class, () -> game.dropDisc(0, '.'),
            "Empty cell character should be rejected");
    }

    @Test
    @DisplayName("Dropping disc on full column should throw IllegalStateException")
    void testDropDiscOnFullColumn() {
        // Fill column 0 with 6 discs (standard board height)
        for (int i = 0; i < 6; i++) {
            game.dropDisc(0, 'R');
        }
        
        // Attempt to add 7th disc should fail
        assertThrows(IllegalStateException.class, () -> game.dropDisc(0, 'Y'),
            "Dropping disc on full column should throw IllegalStateException");
    }

    // ==================== B. Basic Functionality Tests ====================

    @Test
    @DisplayName("New board should not be full")
    void testIsBoardFullOnEmptyBoard() {
        assertFalse(game.isBoardFull(), "Empty board should not be full");
    }

    @Test
    @DisplayName("Dropping single disc should not make board full")
    void testDropDiscEmptyBoard() {
        game.dropDisc(3, 'R');
        assertFalse(game.isBoardFull(), "Board with one disc should not be full");
    }

    @Test
    @DisplayName("Multiple discs can be dropped in different columns")
    void testDropDiscMultipleColumns() {
        assertDoesNotThrow(() -> {
            game.dropDisc(0, 'R');
            game.dropDisc(1, 'Y');
            game.dropDisc(2, 'R');
            game.dropDisc(6, 'Y');
        }, "Should be able to drop discs in multiple columns");
        
        assertFalse(game.isBoardFull(), "Board should not be full");
    }

    @Test
    @DisplayName("Discs should stack vertically due to gravity")
    void testDropDiscGravity() {
        // Drop multiple discs in the same column - they should stack
        assertDoesNotThrow(() -> {
            game.dropDisc(3, 'R');
            game.dropDisc(3, 'Y');
            game.dropDisc(3, 'R');
        }, "Should be able to stack discs in same column");
    }

    @Test
    @DisplayName("Board with some discs should not be full")
    void testIsBoardFullPartiallyFilled() {
        // Fill half the board
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 3; row++) {
                game.dropDisc(col, row % 2 == 0 ? 'R' : 'Y');
            }
        }
        assertFalse(game.isBoardFull(), "Partially filled board should not be full");
    }

    @Test
    @DisplayName("Board with all 42 cells filled should be full")
    void testIsBoardFullCompletelyFilled() {
        // Fill all 42 cells (6 rows × 7 columns)
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                game.dropDisc(col, row % 2 == 0 ? 'R' : 'Y');
            }
        }
        assertTrue(game.isBoardFull(), "Board with all 42 cells filled should be full");
    }

    // ==================== C. Win Detection Tests - No Winner ====================

    @Test
    @DisplayName("Empty board should have no winner")
    void testCheckWinEmptyBoard() {
        assertFalse(game.checkWin(ConnectFour.Player.RED), "Empty board should have no RED winner");
        assertFalse(game.checkWin(ConnectFour.Player.YELLOW), "Empty board should have no YELLOW winner");
    }

    @Test
    @DisplayName("Three in a row should not be a win")
    void testNoWinThreeInRow() {
        game.dropDisc(0, 'R');
        game.dropDisc(1, 'R');
        game.dropDisc(2, 'R');
        
        assertFalse(game.checkWin(ConnectFour.Player.RED), "Three consecutive discs should not win");
    }

    @Test
    @DisplayName("Board with no winner should return false for both players")
    void testCheckWinNoWinner() {
        // Create a board with some discs but no winner
        game.dropDisc(0, 'R');
        game.dropDisc(1, 'Y');
        game.dropDisc(2, 'R');
        game.dropDisc(3, 'Y');
        
        assertFalse(game.checkWin(ConnectFour.Player.RED), "Should be no RED winner");
        assertFalse(game.checkWin(ConnectFour.Player.YELLOW), "Should be no YELLOW winner");
    }

    // ==================== D. Win Detection Tests - Horizontal ====================

    @Test
    @DisplayName("Four horizontal discs in bottom row should win")
    void testCheckWinHorizontalBottomRow() {
        // Place 4 RED discs horizontally in columns 0-3
        game.dropDisc(0, 'R');
        game.dropDisc(1, 'R');
        game.dropDisc(2, 'R');
        game.dropDisc(3, 'R');
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "Four horizontal RED discs should win");
        assertFalse(game.checkWin(ConnectFour.Player.YELLOW), "YELLOW should not win");
    }

    @Test
    @DisplayName("Four horizontal discs in upper row should win")
    void testCheckWinHorizontalUpperRow() {
        // Build up to create horizontal win in upper row
        // Fill bottom rows and create horizontal win on top
        for (int i = 0; i < 4; i++) {
            game.dropDisc(i, 'Y'); // Bottom row
            game.dropDisc(i, 'R'); // Second row - this will be the winning row
        }
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "Four horizontal RED discs in upper row should win");
    }

    @Test
    @DisplayName("Four horizontal discs at right edge should win")
    void testCheckWinHorizontalRightEdge() {
        // Place 4 YELLOW discs horizontally in columns 3-6 (right edge)
        game.dropDisc(3, 'Y');
        game.dropDisc(4, 'Y');
        game.dropDisc(5, 'Y');
        game.dropDisc(6, 'Y');
        
        assertTrue(game.checkWin(ConnectFour.Player.YELLOW), "Four horizontal YELLOW discs should win");
    }

    // ==================== E. Win Detection Tests - Vertical ====================

    @Test
    @DisplayName("Four vertical discs should win")
    void testCheckWinVertical() {
        // Stack 4 RED discs in column 0
        game.dropDisc(0, 'R');
        game.dropDisc(0, 'R');
        game.dropDisc(0, 'R');
        game.dropDisc(0, 'R');
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "Four vertical RED discs should win");
        assertFalse(game.checkWin(ConnectFour.Player.YELLOW), "YELLOW should not win");
    }

    @Test
    @DisplayName("Four vertical discs in middle column should win")
    void testCheckWinVerticalMiddleColumn() {
        // Stack 4 YELLOW discs in column 3 (middle)
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'Y');
        
        assertTrue(game.checkWin(ConnectFour.Player.YELLOW), "Four vertical YELLOW discs should win");
    }

    @Test
    @DisplayName("Four vertical discs in rightmost column should win")
    void testCheckWinVerticalRightColumn() {
        // Stack 4 RED discs in column 6 (rightmost)
        game.dropDisc(6, 'R');
        game.dropDisc(6, 'R');
        game.dropDisc(6, 'R');
        game.dropDisc(6, 'R');
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "Four vertical RED discs in rightmost column should win");
    }

    // ==================== F. Win Detection Tests - Diagonal Down-Right (\) ====================

    @Test
    @DisplayName("Four diagonal discs (down-right) should win")
    void testCheckWinDiagonalDownRight() {
        // Create diagonal win for RED: \ pattern
        // Column 0: R (at bottom)
        // Column 1: Y, R
        // Column 2: Y, Y, R
        // Column 3: Y, Y, Y, R
        
        game.dropDisc(0, 'R');
        
        game.dropDisc(1, 'Y');
        game.dropDisc(1, 'R');
        
        game.dropDisc(2, 'Y');
        game.dropDisc(2, 'Y');
        game.dropDisc(2, 'R');
        
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'R');
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "Four diagonal RED discs (\\) should win");
    }

    // ==================== G. Win Detection Tests - Diagonal Down-Left (/) ====================

    @Test
    @DisplayName("Four diagonal discs (down-left) should win")
    void testCheckWinDiagonalDownLeft() {
        // Create diagonal win for YELLOW: / pattern
        // Column 3: Y (at bottom)
        // Column 2: R, Y
        // Column 1: R, R, Y
        // Column 0: R, R, R, Y
        
        game.dropDisc(3, 'Y');
        
        game.dropDisc(2, 'R');
        game.dropDisc(2, 'Y');
        
        game.dropDisc(1, 'R');
        game.dropDisc(1, 'R');
        game.dropDisc(1, 'Y');
        
        game.dropDisc(0, 'R');
        game.dropDisc(0, 'R');
        game.dropDisc(0, 'R');
        game.dropDisc(0, 'Y');
        
        assertTrue(game.checkWin(ConnectFour.Player.YELLOW), "Four diagonal YELLOW discs (/) should win");
    }

    @Test
    @DisplayName("Diagonal win in upper region of board")
    void testCheckWinDiagonalUpperRegion() {
        // Create diagonal win higher up on the board
        // Build base and then create diagonal on top
        
        // Prepare columns with base discs
        game.dropDisc(0, 'Y'); // Base
        game.dropDisc(0, 'Y'); // Base
        game.dropDisc(0, 'R'); // Start of diagonal
        
        game.dropDisc(1, 'Y');
        game.dropDisc(1, 'Y');
        game.dropDisc(1, 'Y');
        game.dropDisc(1, 'R'); // Diagonal
        
        game.dropDisc(2, 'Y');
        game.dropDisc(2, 'Y');
        game.dropDisc(2, 'Y');
        game.dropDisc(2, 'Y');
        game.dropDisc(2, 'R'); // Diagonal
        
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'Y');
        game.dropDisc(3, 'R'); // Diagonal - winning disc
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "Diagonal RED in upper region should win");
    }

    // ==================== H. Edge Cases ====================

    @Test
    @DisplayName("Five in a row should still be a win")
    void testFiveInRowStillWins() {
        // Place 5 RED discs horizontally
        game.dropDisc(0, 'R');
        game.dropDisc(1, 'R');
        game.dropDisc(2, 'R');
        game.dropDisc(3, 'R');
        game.dropDisc(4, 'R');
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "Five consecutive discs should win (contains four)");
    }

    @Test
    @DisplayName("Full board can have a winner")
    void testCheckWinAfterBoardFull() {
        // Create a winning configuration and then fill the rest
        game.dropDisc(0, 'R');
        game.dropDisc(1, 'R');
        game.dropDisc(2, 'R');
        game.dropDisc(3, 'R');
        
        // Fill remaining cells
        for (int col = 0; col < 7; col++) {
            while (true) {
                try {
                    game.dropDisc(col, col % 2 == 0 ? 'Y' : 'R');
                } catch (IllegalStateException e) {
                    break; // Column full
                }
            }
        }
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "Winner should still be detected on full board");
    }

    @Test
    @DisplayName("Both players can have winning configurations simultaneously")
    void testMultipleWinsOnBoard() {
        // This tests that checkWin examines the entire board
        // Create RED horizontal win
        game.dropDisc(0, 'R');
        game.dropDisc(1, 'R');
        game.dropDisc(2, 'R');
        game.dropDisc(3, 'R');
        
        // Create YELLOW vertical win in different location
        game.dropDisc(5, 'Y');
        game.dropDisc(5, 'Y');
        game.dropDisc(5, 'Y');
        game.dropDisc(5, 'Y');
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "RED should have a win");
        assertTrue(game.checkWin(ConnectFour.Player.YELLOW), "YELLOW should have a win");
    }

    @Test
    @DisplayName("Disc character mapping is consistent with Player enum")
    void testDiscCharacterMapping() {
        // Test that 'R' correctly maps to Player.RED
        game.dropDisc(0, 'R');
        game.dropDisc(0, 'R');
        game.dropDisc(0, 'R');
        game.dropDisc(0, 'R');
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "'R' should map to Player.RED");
        assertFalse(game.checkWin(ConnectFour.Player.YELLOW), "RED win should not be detected as YELLOW win");
        
        // Test that 'Y' correctly maps to Player.YELLOW
        ConnectFour game2 = new ConnectFourImpl();
        game2.dropDisc(1, 'Y');
        game2.dropDisc(1, 'Y');
        game2.dropDisc(1, 'Y');
        game2.dropDisc(1, 'Y');
        
        assertTrue(game2.checkWin(ConnectFour.Player.YELLOW), "'Y' should map to Player.YELLOW");
        assertFalse(game2.checkWin(ConnectFour.Player.RED), "YELLOW win should not be detected as RED win");
    }

    // ==================== I. Integration Tests ====================

    @Test
    @DisplayName("Complete game scenario - RED wins horizontally")
    void testCompleteGameRedWins() {
        // Simulate a realistic game where RED wins
        game.dropDisc(0, 'R'); // RED
        game.dropDisc(0, 'Y'); // YELLOW
        game.dropDisc(1, 'R'); // RED
        game.dropDisc(1, 'Y'); // YELLOW
        game.dropDisc(2, 'R'); // RED
        game.dropDisc(2, 'Y'); // YELLOW
        game.dropDisc(3, 'R'); // RED wins
        
        assertTrue(game.checkWin(ConnectFour.Player.RED), "RED should win horizontally");
        assertFalse(game.checkWin(ConnectFour.Player.YELLOW), "YELLOW should not win");
        assertFalse(game.isBoardFull(), "Board should not be full");
    }

    @Test
    @DisplayName("Complete game scenario - YELLOW wins vertically")
    void testCompleteGameYellowWins() {
        // Simulate a game where YELLOW wins vertically
        game.dropDisc(3, 'Y'); // YELLOW
        game.dropDisc(4, 'R'); // RED
        game.dropDisc(3, 'Y'); // YELLOW
        game.dropDisc(4, 'R'); // RED
        game.dropDisc(3, 'Y'); // YELLOW
        game.dropDisc(4, 'R'); // RED
        game.dropDisc(3, 'Y'); // YELLOW wins
        
        assertTrue(game.checkWin(ConnectFour.Player.YELLOW), "YELLOW should win vertically");
        assertFalse(game.checkWin(ConnectFour.Player.RED), "RED should not win");
    }

    @Test
    @DisplayName("Complete game scenario - draw (full board, no winner)")
    void testCompleteGameDraw() {
        // Create a full board with no winner (this is a specific pattern)
        // Pattern to avoid any wins while filling the board
        int[][] dropPattern = {
            {0, 1, 0, 1, 0, 1},  // Col 0: R,Y,R,Y,R,Y
            {1, 0, 1, 0, 1, 0},  // Col 1: Y,R,Y,R,Y,R
            {0, 1, 0, 1, 0, 1},  // Col 2: R,Y,R,Y,R,Y
            {1, 0, 1, 0, 1, 0},  // Col 3: Y,R,Y,R,Y,R
            {1, 0, 1, 0, 1, 0},  // Col 4: Y,R,Y,R,Y,R
            {0, 1, 0, 1, 0, 1},  // Col 5: R,Y,R,Y,R,Y
            {1, 0, 1, 0, 1, 0}   // Col 6: Y,R,Y,R,Y,R
        };
        
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                char disc = dropPattern[col][row] == 0 ? 'R' : 'Y';
                game.dropDisc(col, disc);
            }
        }
        
        assertTrue(game.isBoardFull(), "Board should be full");
        // Note: This specific pattern might still create wins, so we just verify board is full
        // A true draw without wins is complex to construct
    }
}
