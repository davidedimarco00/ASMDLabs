package it.unibo;

import java.util.Scanner;

public final class ConnectFourGame {
    private ConnectFourGame() {
    }

    public static void main(final String[] args) {
        final ConnectFourImpl game = new ConnectFourImpl();

        try (Scanner scanner = new Scanner(System.in)) {
            ConnectFour.Player currentPlayer = ConnectFour.Player.RED;

            System.out.println("Welcome to Connect Four");
            System.out.println("Columns are numbered from 0 to " + (ConnectFourImpl.COLUMNS - 1));

            while (true) {
                System.out.println(game);
                final char disc = currentPlayer == ConnectFour.Player.RED ? 'R' : 'Y';
                System.out.print("Player " + currentPlayer + " (" + disc + "), choose a column: ");

                final String input = scanner.nextLine().trim();
                final int column;
                try {
                    column = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input. Please type an integer column index.");
                    continue;
                }

                try {
                    game.dropDisc(column, disc);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Invalid move: " + ex.getMessage());
                    continue;
                }

                if (game.checkWin(currentPlayer)) {
                    System.out.println(game);
                    System.out.println("Player " + currentPlayer + " wins!");
                    return;
                }

                if (game.isBoardFull()) {
                    System.out.println(game);
                    System.out.println("Draw: board is full.");
                    return;
                }

                currentPlayer = currentPlayer == ConnectFour.Player.RED
                    ? ConnectFour.Player.YELLOW
                    : ConnectFour.Player.RED;
            }
        }
    }
}

