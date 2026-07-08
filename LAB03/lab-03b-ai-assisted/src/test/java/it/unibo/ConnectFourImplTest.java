package it.unibo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectFourImplTest {

    @Test
    void dropsDiscToLowestAvailableCell() {
        final ConnectFourImpl game = new ConnectFourImpl();

        game.dropDisc(3, 'R');
        game.dropDisc(3, 'Y');

        final char[][] board = game.getBoardCopy();
        assertTrue(board[ConnectFourImpl.ROWS - 1][3] == 'R');
        assertTrue(board[ConnectFourImpl.ROWS - 2][3] == 'Y');
    }

    @Test
    void detectsHorizontalWin() {
        final ConnectFourImpl game = new ConnectFourImpl();

        game.dropDisc(0, 'R');
        game.dropDisc(1, 'R');
        game.dropDisc(2, 'R');
        game.dropDisc(3, 'R');

        assertTrue(game.checkWin(ConnectFour.Player.RED));
    }

    @Test
    void detectsVerticalWin() {
        final ConnectFourImpl game = new ConnectFourImpl();

        game.dropDisc(2, 'Y');
        game.dropDisc(2, 'Y');
        game.dropDisc(2, 'Y');
        game.dropDisc(2, 'Y');

        assertTrue(game.checkWin(ConnectFour.Player.YELLOW));
    }

    @Test
    void detectsDiagonalWin() {
        final ConnectFourImpl game = new ConnectFourImpl();

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

        assertTrue(game.checkWin(ConnectFour.Player.RED));
    }

    @Test
    void rejectsOutOfRangeOrInvalidDisc() {
        final ConnectFourImpl game = new ConnectFourImpl();

        assertThrows(IllegalArgumentException.class, () -> game.dropDisc(-1, 'R'));
        assertThrows(IllegalArgumentException.class, () -> game.dropDisc(ConnectFourImpl.COLUMNS, 'R'));
        assertThrows(IllegalArgumentException.class, () -> game.dropDisc(0, 'Z'));
    }

    @Test
    void detectsFullBoard() {
        final ConnectFourImpl game = new ConnectFourImpl();

        for (int col = 0; col < ConnectFourImpl.COLUMNS; col++) {
            for (int row = 0; row < ConnectFourImpl.ROWS; row++) {
                game.dropDisc(col, (row + col) % 2 == 0 ? 'R' : 'Y');
            }
        }

        assertTrue(game.isBoardFull());
    }
}


