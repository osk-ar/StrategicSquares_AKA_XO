package com.example.tictactoe.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AiClass {

    // Easy AI
    public static int getEasyMove(String[] board) {
        List<Integer> availableMoves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            if (board[i].equals(" ")) {
                availableMoves.add(i);
            }
        }
        return availableMoves.get(new Random().nextInt(availableMoves.size()));
    }

    // Medium AI
    public static int getMediumMove(String[] board, String aiPlayer, String humanPlayer) {
        // Try to win
        int winningMove = findWinningMove(board, aiPlayer);
        if (winningMove != -1) return winningMove;

        // Try to block opponent's win
        int blockingMove = findWinningMove(board, humanPlayer);
        if (blockingMove != -1) return blockingMove;

        // Random move if no win/block
        return getEasyMove(board);
    }

    private static int findWinningMove(String[] board, String player) {
        for (int i = 0; i < board.length; i++) {
            if (board[i].equals(" ")) {
                board[i] = player;
                if (checkWin(board, player)) {
                    board[i] = " ";
                    return i;
                }
                board[i] = " ";
            }
        }
        return -1;
    }

    private static boolean checkWin(String[] board, String player) {
        int[][] winConditions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // Columns
                {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        for (int[] condition : winConditions) {
            if (board[condition[0]].equals(player) &&
                    board[condition[1]].equals(player) &&
                    board[condition[2]].equals(player)) {
                return true;
            }
        }
        return false;
    }

    // Hard AI
    public static int getHardMove(String[] board, String aiPlayer, String humanPlayer) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < board.length; i++) {
            if (board[i].equals(" ")) {
                board[i] = aiPlayer;
                int score = minimax(board, 0, false, aiPlayer, humanPlayer);
                board[i] = " ";
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
        return bestMove;
    }

    private static int minimax(String[] board, int depth, boolean isMaximizing, String aiPlayer, String humanPlayer) {
        String winner = getWinner(board);
        if (winner.equals(aiPlayer)) return 10 - depth;
        if (winner.equals(humanPlayer)) return depth - 10;
        if (isBoardFull(board)) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < board.length; i++) {
                if (board[i].equals(" ")) {
                    board[i] = aiPlayer;
                    int score = minimax(board, depth + 1, false, aiPlayer, humanPlayer);
                    board[i] = " ";
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < board.length; i++) {
                if (board[i].equals(" ")) {
                    board[i] = humanPlayer;
                    int score = minimax(board, depth + 1, true, aiPlayer, humanPlayer);
                    board[i] = " ";
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private static String getWinner(String[] board) {
        int[][] winConditions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
                {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        for (int[] condition : winConditions) {
            if (board[condition[0]].equals(board[condition[1]]) &&
                    board[condition[1]].equals(board[condition[2]]) &&
                    !board[condition[0]].equals(" ")) {
                return board[condition[0]];
            }
        }
        return " ";
    }

    private static boolean isBoardFull(String[] board) {
        for (String spot : board) {
            if (spot.equals(" ")) {
                return false;
            }
        }
        return true;
    }

}
