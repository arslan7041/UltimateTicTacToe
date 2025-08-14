package com.example.ultimatetictactoe.artificialintelligence;

import com.example.ultimatetictactoe.UltimateTicTacToeBackEndGame;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

@RequiredArgsConstructor
public class ArtificialIntelligenceEngine {
    private GameState game;
    private final GridPane mainGrid;
    private int minimaxCalls = 0;
    private static final Random random = new Random();

    public BestMove getBestAIMove(UltimateTicTacToeBackEndGame game, Set<Node> clickableMiniGrids) {
        ModelMapper modelMapper = ModelMapperSingleton.getInstance();
        this.game = modelMapper.map(game, GameState.class);
        this.game.getPlayer1().hasWonGame(game.getPlayer1().hasWonGame());
        this.game.getPlayer2().hasWonGame(game.getPlayer2().hasWonGame());
        this.game.setClickableMiniGrids(new HashSet<>(clickableMiniGrids));
        this.game.setMainGrid(mainGrid);

        int depth = 3;
        minimaxCalls = 0;
        List<BestMove> bestMoves = minimax(depth, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        System.out.println("Number of minimax calls: " + minimaxCalls);
        return bestMoves.get( random.nextInt(bestMoves.size()) );
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
                game.simulateTurn(move.getButton(), move.getMiniGrid(), true);
                List<BestMove> tempBestMoves = minimax(depth - 1, false, alpha, beta);
                game.undoTurn(move.getButton(), move.getMiniGrid());
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
                game.simulateTurn(move.getButton(), move.getMiniGrid(), false);
                List<BestMove> tempBestMoves = minimax(depth - 1, true, alpha, beta);
                game.undoTurn(move.getButton(), move.getMiniGrid());
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
