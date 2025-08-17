package com.example.ultimatetictactoe.artificialintelligence;

import com.example.ultimatetictactoe.Player;
import com.example.ultimatetictactoe.UltimateTicTacToeBackEndGame;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ArtificialIntelligenceEngine {
    private GameState game;
    private int minimaxCalls = 0;
    private static final Random random = new Random();
    private int maxDepth;

    public BestMove getBestAIMove(UltimateTicTacToeBackEndGame game, Set<Node> clickableMiniGrids) {
        this.game = new GameState();
        this.game.setPlayer1( mapPlayer(game.getPlayer1()) );
        this.game.setPlayer2( mapPlayer(game.getPlayer2()) );
        this.game.setGrid( mapGrid(game.getGrid()) );
        this.game.setMiniGridWinsBoard( mapMiniGridWinsBoard(game.getMiniGridWinsBoard()) );
        this.game.setTie( game.isTie() );
        this.game.setClickableMiniGrids( mapClickableMiniGrids(clickableMiniGrids) );
        this.game.setMaximizingPlayer( false );

        maxDepth = 9;
        minimaxCalls = 0;
        List<BestMove> bestMoves = minimax(0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        System.out.println("Number of minimax calls: " + minimaxCalls);
//        List<BestMove> immediateWins = new ArrayList<>();
//        for (BestMove bestMove : bestMoves) {
//            if (bestMove.getMove() != null) {
//                this.game.simulateTurn(bestMove.getMove(), false);
//                boolean won = this.game.getPlayer2().hasWonGame();
//                this.game.undoTurn();
//                if (won) {
//                    immediateWins.add(bestMove);
//                }
//            }
//        }
//
//        if (!immediateWins.isEmpty()) {
//            return immediateWins.get(random.nextInt(immediateWins.size()));
//        }

        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private Set<Coordinates> mapClickableMiniGrids(Set<Node> clickableMiniGridsSource) {
        if(clickableMiniGridsSource == null) {
            return null;
        }

        Set<Coordinates> clickableMiniGridsDest = new HashSet<>();
        for(Node miniGrid : clickableMiniGridsSource) {
            int miniGridRow = GridPane.getRowIndex(miniGrid);
            int miniGridCol = GridPane.getColumnIndex(miniGrid);

            clickableMiniGridsDest.add( new Coordinates(miniGridRow, miniGridCol) );
        }
        return clickableMiniGridsDest;
    }

    private PlayerState mapPlayer(Player player) {
        if(player == null) {
            return null;
        }

        PlayerState playerState = new PlayerState();
        playerState.hasWonGame(player.hasWonGame());
        playerState.setMiniGridWins(player.getMiniGridWins());

        return playerState;
    }

    private int[][][][] mapGrid(int[][][][] sourceGrid) {
        if(sourceGrid == null) {
            return null;
        }

        int[][][][] dest = new int[3][3][3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    System.arraycopy(sourceGrid[i][j][k], 0, dest[i][j][k], 0, 3);
                }
            }
        }
        return dest;
    }

    private int[][] mapMiniGridWinsBoard(int[][] sourceGrid) {
        if (sourceGrid == null) return null;

        int[][] dest = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(sourceGrid[i], 0, dest[i], 0, 3);
        }
        return dest;
    }



    private List<BestMove> minimax(int depth, boolean maximizingPlayer, int alpha, int beta) {
        minimaxCalls++;
        if (depth == maxDepth || isGameOver()) {
            return List.of(new BestMove(evaluate(depth), null));
        }

        List<Move> availableMoves = game.getAvailableMoves();
        List<BestMove> bestMoves = new ArrayList<>();

        if (maximizingPlayer) { // player1 = maximising player
            int maxEval = Integer.MIN_VALUE;
            for (Move move : availableMoves) {
                game.simulateTurn(move, true);
                List<BestMove> tempBestMoves = minimax(depth + 1, false, alpha, beta);
                game.undoTurn();
                BestMove b = tempBestMoves.get(0);
                if(b.getScore() == maxEval){
                    bestMoves.add(new BestMove(b.getScore(), move));
                } else if (b.getScore() > maxEval) {
                    maxEval = b.getScore();
                    bestMoves.clear();
                    bestMoves.add(new BestMove(b.getScore(), move));
                }
                alpha = max(alpha, b.getScore());
                if(beta <= alpha){
                    break;
                }
            }
            return bestMoves;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : availableMoves) {
                game.simulateTurn(move, false);
                List<BestMove> tempBestMoves = minimax(depth + 1, true, alpha, beta);
                game.undoTurn();
                BestMove b = tempBestMoves.get(0);
                if(b.getScore() == minEval){
                    bestMoves.add(new BestMove(b.getScore(), move));
                } else if (b.getScore() < minEval) {
                    minEval = b.getScore();
                    bestMoves.clear();
                    bestMoves.add(new BestMove(b.getScore(), move));
                }
                beta = min(beta, b.getScore());
                if(beta <= alpha){
                    break;
                }
            }
            return bestMoves;
        }
    }

    private int evaluate(int depth) {
        return heur1(depth);
    }

    private int heur1(int depth) {
        if (game.isTie()) {
            return 0;
        }

        if (game.getPlayer1().hasWonGame()) {
            return 10000 - depth;
        }

        if (game.getPlayer2().hasWonGame()) {
            return -10000 + depth;
        }

        int score = 0;

        // mini‑grid win difference
        score += (game.getPlayer1().getMiniGridWins() - game.getPlayer2().getMiniGridWins()) * 100;

        // centre mini‑grid control on the main board
        if (game.getMiniGridWinsBoard()[1][1] == 1) {
            score += 50;
        } else if (game.getMiniGridWinsBoard()[1][1] == 2) {
            score -= 50;
        }

        // two in a row and fork potential on the main board
        score += evaluateLines(game.getMiniGridWinsBoard(), 100);

        // evaluate each mini‑grid that is still playable
        int[][][][] grid = game.getGrid();
        int[][] miniGridWins = game.getMiniGridWinsBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (miniGridWins[i][j] == 0) {
                    score += evaluateMiniGrid(grid[i][j]);
                }
            }
        }

        // ability to play anywhere next turn
//        if (game.getClickableMiniGrids() != null && game.getClickableMiniGrids().size() > 1) {
//            // if it is player1's turn (maximizingPlayer == false), a free move is good
//            if (!game.isMaximizingPlayer()) {
//                score += 15;
//            } else {
//                score -= 15;
//            }
//        }

        return score;
    }

    private int evaluateMiniGrid(int[][] miniGrid) {
        int score = 0;

        // centre control inside the mini‑grid
        if (miniGrid[1][1] == 1) {
            score += 3;
        } else if (miniGrid[1][1] == 2) {
            score -= 3;
        }

        // corners encourage multiple winning possibilities
        int[] corners = {miniGrid[0][0], miniGrid[0][2], miniGrid[2][0], miniGrid[2][2]};
        for (int val : corners) {
            if (val == 1) {
                score += 1;
            } else if (val == 2) {
                score -= 1;
            }
        }

        // two in a row and potential forks within the mini‑grid
        score += evaluateLines(miniGrid, 10);

        return score;
    }

    private int evaluateLines(int[][] board, int weight) {
        int score = 0;

        // rows and columns
        for (int i = 0; i < 3; i++) {
            score += evaluateLine(board[i][0], board[i][1], board[i][2], weight);
            score += evaluateLine(board[0][i], board[1][i], board[2][i], weight);
        }

        // diagonals
        score += evaluateLine(board[0][0], board[1][1], board[2][2], weight);
        score += evaluateLine(board[0][2], board[1][1], board[2][0], weight);

        return score;
    }

    private int evaluateLine(int a, int b, int c, int weight) {
        int[] vals = {a, b, c};
        int p1 = 0;
        int p2 = 0;
        int empty = 0;
        for (int v : vals) {
            if (v == 1) {
                p1++;
            } else if (v == 2) {
                p2++;
            } else if (v == 0) {
                empty++;
            } else {
                // -1 means the line is blocked
                return 0;
            }
        }

        if (p1 == 2 && empty == 1) {
            return weight;
        }
        if (p2 == 2 && empty == 1) {
            return -weight;
        }
        if (p1 == 1 && empty == 2) {
            return weight / 3;
        }
        if (p2 == 1 && empty == 2) {
            return -weight / 3;
        }
        return 0;
    }

    private boolean isGameOver(){
        return game.getPlayer1().hasWonGame() || game.getPlayer2().hasWonGame() || game.isTie();
    }
}
