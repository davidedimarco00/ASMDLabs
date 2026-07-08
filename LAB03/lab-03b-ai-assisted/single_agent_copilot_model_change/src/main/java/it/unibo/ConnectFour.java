package it.unibo;

public interface ConnectFour {
    enum Player {
        RED, YELLOW
    }
    void dropDisc(int column, char disc);
    boolean checkWin(Player player);
    boolean isBoardFull();
}
