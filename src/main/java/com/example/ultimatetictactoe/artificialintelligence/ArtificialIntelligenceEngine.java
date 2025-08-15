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

    public BestMove getBestAIMove(UltimateTicTacToeBackEndGame game, Set<Node> clickableMiniGrids) {
        this.game = new GameState();
        this.game.setPlayer1( mapPlayer(game.getPlayer1()) );
        this.game.setPlayer2( mapPlayer(game.getPlayer2()) );
        this.game.setGrid( mapGrid(game.getGrid()) );
        this.game.setMiniGridWinsBoard( mapMiniGridWinsBoard(game.getMiniGridWinsBoard()) );
        this.game.setTie( game.isTie() );
        this.game.setClickableMiniGrids( mapClickableMiniGrids(clickableMiniGrids) );
        this.game.setMaximizingPlayer( false );

        int depth = 3;
        minimaxCalls = 0;
        List<BestMove> bestMoves = minimax(depth, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        System.out.println("Number of minimax calls: " + minimaxCalls);
        return bestMoves.get( random.nextInt(bestMoves.size()) );
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
        if (depth == 0 || isGameOver()) {
            return List.of(new BestMove(evaluate(), null));
        }

        List<Move> availableMoves = game.getAvailableMoves();
        List<BestMove> bestMoves = new ArrayList<>();

        if (maximizingPlayer) { // player1 = maximising player
            int maxEval = Integer.MIN_VALUE;
            for (Move move : availableMoves) {
                System.out.println("player 1");
                System.out.println("Depth = " + depth);
                game.simulateTurn(move, true);
                List<BestMove> tempBestMoves = minimax(depth - 1, false, alpha, beta);
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
                System.out.println("player 2");
                System.out.println("Depth = " + depth);
                game.simulateTurn(move, false);
                List<BestMove> tempBestMoves = minimax(depth - 1, true, alpha, beta);
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

    private int evaluate() {
        return heur1();
    }

    private int heur1() {
        if(game.isTie() ){
//            game.printUltimateTicTacToeGrid();
            return 0;
        }

        if(game.getPlayer1().hasWonGame()){
//            game.printUltimateTicTacToeGrid();
            return 10000;
        }

        if(game.getPlayer2().hasWonGame()){
//            game.printUltimateTicTacToeGrid();
            return -10000;
        }

//            game.printUltimateTicTacToeGrid();
        return game.getPlayer1().getMiniGridWins() - game.getPlayer2().getMiniGridWins();

    }

    private boolean isGameOver(){
        return game.getPlayer1().hasWonGame() || game.getPlayer2().hasWonGame() || game.isTie();
    }
}
