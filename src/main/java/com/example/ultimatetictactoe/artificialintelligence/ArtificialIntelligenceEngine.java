package com.example.ultimatetictactoe.artificialintelligence;

import com.example.ultimatetictactoe.UltimateTicTacToeBackEndGame;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class ArtificialIntelligenceEngine {
    private GameState game;
    private final GridPane mainGrid;
    private int minimaxCalls = 0;

    public BestMove getBestAIMove(UltimateTicTacToeBackEndGame game, Set<Node> clickableMiniGrids) throws Exception {
        ModelMapper modelMapper = ModelMapperSingleton.getInstance();
        this.game = modelMapper.map(game, GameState.class);
        this.game.setClickableMiniGrids(new HashSet<>(clickableMiniGrids));
        this.game.setMainGrid(mainGrid);

        int depth = 9;
        minimaxCalls = 0;
        BestMove result = minimax(depth, false);
        System.out.println("Number of minimax calls: " + minimaxCalls);
        return result;
    }

    private BestMove minimax(int depth, boolean maximizingPlayer) {
        minimaxCalls++;
        if (depth == 0 || isGameOver()) {
            return new BestMove(evaluate(), null);
        }

        BestMove bestMove = new BestMove();

        if (maximizingPlayer) { // player1 = maximising player
            int maxEval = Integer.MIN_VALUE;
            List<Move> availableMoves = game.getAvailableMoves();
            for (Move move : availableMoves) {
                game.simulateTurn(move.getButton(), move.getMiniGrid());
                BestMove m = minimax(depth - 1, false);
                if (m.getScore() > maxEval) {
                    maxEval = m.getScore();
                    bestMove.setScore(maxEval);
                    bestMove.setMove(move);
                }
                game.undoTurn(move.getButton(), move.getMiniGrid());
            }
            return bestMove;
        } else {
            int minEval = Integer.MAX_VALUE;
            List<Move> availableMoves = game.getAvailableMoves();
            for (Move move : availableMoves) {
                game.simulateTurn(move.getButton(), move.getMiniGrid());
                BestMove m = minimax(depth - 1, true);
                if (m.getScore() < minEval) {
                    minEval = m.getScore();
                    bestMove.setScore(minEval);
                    bestMove.setMove(move);
                }
                game.undoTurn(move.getButton(), move.getMiniGrid());
            }
            return bestMove;
        }
    }

    private int evaluate() {
        return heur1();
    }

    private int heur1() {
        if(game.isTie() ){
            return 0;
        }else if(game.getPlayer1().hasWonGame()){
            return 10000;
        }else if(game.getPlayer2().hasWonGame()){
            return -10000;
        }else{
            return game.getPlayer1().getMiniGridWins() - game.getPlayer2().getMiniGridWins();
        }
    }

    private boolean isGameOver(){
        return game.getPlayer1().hasWonGame() || game.getPlayer2().hasWonGame() || game.isTie();
    }
}
