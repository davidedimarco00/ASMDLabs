package it.unibo;

import java.util.Scanner;

/**
 * Console-based Connect Four game application.
 * 
 * Allows two players to play Connect Four via console input.
 * Players alternate turns, choosing columns to drop their discs.
 */
public class ConnectFourGame {
    
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConnectFour game = new ConnectFourImpl();
        
        System.out.println("=== Connect Four ===");
        System.out.println("Players: RED (R) vs YELLOW (Y)");
        System.out.println("Enter column number (0-6) to drop your disc");
        System.out.println();
        
        ConnectFour.Player currentPlayer = ConnectFour.Player.RED;
        boolean gameOver = false;
        
        while (!gameOver && !game.isBoardFull()) {
            printBoard(game);
            System.out.println();
            System.out.println(currentPlayer + "'s turn");
            System.out.print("Enter column (0-6): ");
            
            try {
                if (!scanner.hasNextInt()) {
                    System.out.println("X Invalid input. Please enter a number between 0 and 6.");
                    scanner.nextLine(); // Clear invalid input
                    continue;
                }
                
                int column = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                
                char disc = currentPlayer == ConnectFour.Player.RED ? 'R' : 'Y';
                
                game.dropDisc(column, disc);
                
                if (game.checkWin(currentPlayer)) {
                    printBoard(game);
                    System.out.println();
                    System.out.println("*** " + currentPlayer + " WINS! ***");
                    gameOver = true;
                } else {
                    // Switch player
                    currentPlayer = currentPlayer == ConnectFour.Player.RED 
                        ? ConnectFour.Player.YELLOW 
                        : ConnectFour.Player.RED;
                }
                
            } catch (IllegalArgumentException e) {
                System.out.println("X Invalid input: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("X Invalid move: " + e.getMessage());
            }
        }
        
        if (!gameOver && game.isBoardFull()) {
            printBoard(game);
            System.out.println();
            System.out.println("=== Game ended in a DRAW! ===");
        }
        
        scanner.close();
    }
    
    /**
     * Prints the current board state to the console.
     * Uses reflection to access the board (for display purposes only).
     */
    private static void printBoard(ConnectFour game) {
        System.out.println("  0 1 2 3 4 5 6");
        System.out.println(" +-------------+");
        
        // Since we can't access the board directly, we'll use toString()
        String boardStr = game.toString();
        String[] rows = boardStr.split("\n");
        
        for (int i = 0; i < rows.length; i++) {
            System.out.print(" |");
            String[] cells = rows[i].trim().split(" ");
            for (String cell : cells) {
                if (cell.equals("R")) {
                    System.out.print(ANSI_RED + "R" + ANSI_RESET + " ");
                } else if (cell.equals("Y")) {
                    System.out.print(ANSI_YELLOW + "Y" + ANSI_RESET + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println("|");
        }
        
        System.out.println(" +-------------+");
    }
}
