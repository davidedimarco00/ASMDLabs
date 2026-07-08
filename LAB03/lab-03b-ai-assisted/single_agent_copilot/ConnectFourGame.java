package it.unibo;

import java.util.Scanner;

public class ConnectFourGame {
    public static void main(String[] args) {
        ConnectFourImpl game = new ConnectFourImpl();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Connect Four (CLI)");
        System.out.println("Columns are numbered 0 to " + (ConnectFourImpl.COLUMNS - 1));

        ConnectFour.Player current = ConnectFour.Player.RED;

        while (true) {
            System.out.println(game.toString());
            System.out.println("Player " + (current == ConnectFour.Player.RED ? "RED (R)" : "YELLOW (Y)") + ", choose column:");
            String line = scanner.nextLine();
            int col;
            try {
                col = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid column number.");
                continue;
            }

            try {
                char disc = current == ConnectFour.Player.RED ? 'R' : 'Y';
                game.dropDisc(col, disc);
            } catch (IllegalArgumentException e) {
                System.out.println("Move invalid: " + e.getMessage());
                continue;
            }

            // check win
            if (game.checkWin(current)) {
                System.out.println(game.toString());
                System.out.println("Player " + (current == ConnectFour.Player.RED ? "RED" : "YELLOW") + " wins!");
                break;
            }

            if (game.isBoardFull()) {
                System.out.println(game.toString());
                System.out.println("Board is full. It's a tie!");
                break;
            }

            // switch player
            current = (current == ConnectFour.Player.RED) ? ConnectFour.Player.YELLOW : ConnectFour.Player.RED;
        }

        scanner.close();
    }
}

