package it.unibo;

import java.util.Arrays;

public final class ConnectFourImpl implements ConnectFour {
    public static final int ROWS = 6;
    public static final int COLUMNS = 7;

    private static final char EMPTY = '.';
    private static final char RED_DISC = 'R';
    private static final char YELLOW_DISC = 'Y';

    private final char[][] board = new char[ROWS][COLUMNS];

    public ConnectFourImpl() {
        for (int row = 0; row < ROWS; row++) {
            Arrays.fill(board[row], EMPTY);
        }
    }

    @Override
    public void dropDisc(final int column, final char disc) {
        validateColumn(column);
        validateDisc(disc);

        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == EMPTY) {
                board[row][column] = disc;
                return;
            }
        }
        throw new IllegalArgumentException("Column " + column + " is full");
    }

    @Override
    public boolean checkWin(final Player player) {
        final char disc = player == Player.RED ? RED_DISC : YELLOW_DISC;

        return hasHorizontalWin(disc)
            || hasVerticalWin(disc)
            || hasDiagonalDownRightWin(disc)
            || hasDiagonalUpRightWin(disc);
    }

    @Override
    public boolean isBoardFull() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (board[row][col] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                sb.append(board[row][col]).append(' ');
            }
            sb.append('\n');
        }
        for (int col = 0; col < COLUMNS; col++) {
            sb.append(col).append(' ');
        }
        sb.append('\n');
        return sb.toString();
    }

    public char[][] getBoardCopy() {
        final char[][] copy = new char[ROWS][COLUMNS];
        for (int row = 0; row < ROWS; row++) {
            System.arraycopy(board[row], 0, copy[row], 0, COLUMNS);
        }
        return copy;
    }

    private void validateColumn(final int column) {
        if (column < 0 || column >= COLUMNS) {
            throw new IllegalArgumentException("Column out of range: " + column);
        }
    }

    private void validateDisc(final char disc) {
        if (disc != RED_DISC && disc != YELLOW_DISC) {
            throw new IllegalArgumentException("Disc must be 'R' or 'Y'");
        }
    }

    private boolean hasHorizontalWin(final char disc) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col <= COLUMNS - 4; col++) {
                if (board[row][col] == disc
                    && board[row][col + 1] == disc
                    && board[row][col + 2] == disc
                    && board[row][col + 3] == disc) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasVerticalWin(final char disc) {
        for (int col = 0; col < COLUMNS; col++) {
            for (int row = 0; row <= ROWS - 4; row++) {
                if (board[row][col] == disc
                    && board[row + 1][col] == disc
                    && board[row + 2][col] == disc
                    && board[row + 3][col] == disc) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasDiagonalDownRightWin(final char disc) {
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLUMNS - 4; col++) {
                if (board[row][col] == disc
                    && board[row + 1][col + 1] == disc
                    && board[row + 2][col + 2] == disc
                    && board[row + 3][col + 3] == disc) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasDiagonalUpRightWin(final char disc) {
        for (int row = 3; row < ROWS; row++) {
            for (int col = 0; col <= COLUMNS - 4; col++) {
                if (board[row][col] == disc
                    && board[row - 1][col + 1] == disc
                    && board[row - 2][col + 2] == disc
                    && board[row - 3][col + 3] == disc) {
                    return true;
                }
            }
        }
        return false;
    }
}

