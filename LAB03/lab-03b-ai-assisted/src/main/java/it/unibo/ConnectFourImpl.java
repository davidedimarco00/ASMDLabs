package it.unibo;

/**
 * Implementation of the Connect Four game.
 * 
 * This implementation follows standard Connect Four rules:
 * - Board size: 6 rows × 7 columns
 * - Win condition: 4 consecutive discs (horizontal, vertical, or diagonal)
 * - Gravity: discs fall to the lowest available row
 * - Disc mapping: 'R' for RED, 'Y' for YELLOW
 * 
 * Design decisions:
 * - No turn enforcement (caller's responsibility)
 * - No game-over state (moves allowed after win)
 * - Exception-based error handling for invalid inputs
 */
public class ConnectFourImpl implements ConnectFour {
    
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final int WIN_LENGTH = 4;
    private static final char EMPTY = '.';
    
    private final char[][] board;
    
    /**
     * Creates a new Connect Four game with an empty board.
     */
    public ConnectFourImpl() {
        this.board = new char[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                board[row][col] = EMPTY;
            }
        }
    }
    
    @Override
    public void dropDisc(int column, char disc) {
        // Validate column
        if (column < 0 || column >= COLS) {
            throw new IllegalArgumentException("Invalid column: " + column);
        }
        
        // Validate disc character
        if (disc != 'R' && disc != 'Y') {
            throw new IllegalArgumentException("Invalid disc: " + disc);
        }
        
        // Find lowest available row
        int row = findLowestRow(column);
        if (row == -1) {
            throw new IllegalStateException("Column " + column + " is full");
        }
        
        // Place disc
        board[row][column] = disc;
    }
    
    @Override
    public boolean checkWin(Player player) {
        char disc = playerToChar(player);
        
        // Check all positions on the board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == disc) {
                    // Check four directions from this position
                    if (checkDirection(row, col, 0, 1, disc) ||   // Horizontal
                        checkDirection(row, col, 1, 0, disc) ||   // Vertical
                        checkDirection(row, col, 1, 1, disc) ||   // Diagonal down-right
                        checkDirection(row, col, 1, -1, disc)) {  // Diagonal down-left
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean isBoardFull() {
        // Board is full if the top row has no empty cells
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Finds the lowest available row in a column.
     * 
     * @param column the column to check
     * @return the row index of the lowest empty cell, or -1 if column is full
     */
    private int findLowestRow(int column) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == EMPTY) {
                return row;
            }
        }
        return -1; // Column is full
    }
    
    /**
     * Checks for WIN_LENGTH consecutive discs in a specific direction.
     * 
     * @param startRow starting row position
     * @param startCol starting column position
     * @param dRow row direction increment
     * @param dCol column direction increment
     * @param disc the disc character to check for
     * @return true if WIN_LENGTH consecutive discs found
     */
    private boolean checkDirection(int startRow, int startCol, int dRow, int dCol, char disc) {
        int count = 0;
        
        for (int i = 0; i < WIN_LENGTH; i++) {
            int row = startRow + i * dRow;
            int col = startCol + i * dCol;
            
            // Check bounds
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                return false;
            }
            
            // Check if cell matches
            if (board[row][col] == disc) {
                count++;
            } else {
                return false;
            }
        }
        
        return count >= WIN_LENGTH;
    }
    
    /**
     * Converts a Player enum to its corresponding disc character.
     * 
     * @param player the player
     * @return 'R' for RED, 'Y' for YELLOW
     */
    private char playerToChar(Player player) {
        return player == Player.RED ? 'R' : 'Y';
    }
    
    /**
     * Returns a string representation of the board for debugging.
     * 
     * @return string representation of the board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                sb.append(board[row][col]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
