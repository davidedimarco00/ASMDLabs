package it.unibo;

import java.util.Arrays;

public class ConnectFourImpl implements ConnectFour {
    // Standard Connect Four board size
    public static final int ROWS = 6;
    public static final int COLUMNS = 7;

    private final char[][] board;
    private static final char EMPTY = '.';

    public ConnectFourImpl() {
        this.board = new char[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            Arrays.fill(this.board[r], EMPTY);
        }
    }

    @Override
    public void dropDisc(int column, char disc) {
        if (column < 0 || column >= COLUMNS) {
            throw new IllegalArgumentException("Column out of range: " + column);
        }
        if (disc == '\0') {
            throw new IllegalArgumentException("Disc cannot be null char");
        }

        // place disc in the lowest available row in the column
        for (int r = ROWS - 1; r >= 0; r--) {
            if (board[r][column] == EMPTY) {
                board[r][column] = disc;
                return;
            }
        }
        throw new IllegalArgumentException("Column " + column + " is full");
    }

    @Override
    public boolean checkWin(Player player) {
        char disc = player == Player.RED ? 'R' : 'Y';
        // horizontal
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c <= COLUMNS - 4; c++) {
                if (board[r][c] == disc && board[r][c + 1] == disc && board[r][c + 2] == disc && board[r][c + 3] == disc) {
                    return true;
                }
            }
        }
        // vertical
        for (int c = 0; c < COLUMNS; c++) {
            for (int r = 0; r <= ROWS - 4; r++) {
                if (board[r][c] == disc && board[r + 1][c] == disc && board[r + 2][c] == disc && board[r + 3][c] == disc) {
                    return true;
                }
            }
        }
        // diagonal down-right
        for (int r = 0; r <= ROWS - 4; r++) {
            for (int c = 0; c <= COLUMNS - 4; c++) {
                if (board[r][c] == disc && board[r + 1][c + 1] == disc && board[r + 2][c + 2] == disc && board[r + 3][c + 3] == disc) {
                    return true;
                }
            }
        }
        // diagonal up-right
        for (int r = 3; r < ROWS; r++) {
            for (int c = 0; c <= COLUMNS - 4; c++) {
                if (board[r][c] == disc && board[r - 1][c + 1] == disc && board[r - 2][c + 2] == disc && board[r - 3][c + 3] == disc) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isBoardFull() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (board[r][c] == EMPTY) return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                sb.append(board[r][c]).append(' ');
            }
            sb.append('\n');
        }
        // column indices
        for (int c = 0; c < COLUMNS; c++) {
            sb.append(c).append(' ');
        }
        sb.append('\n');
        return sb.toString();
    }

    // helper accessor for tests or printing
    public char[][] getBoard() {
        char[][] copy = new char[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) System.arraycopy(board[r], 0, copy[r], 0, COLUMNS);
        return copy;
    }
}

