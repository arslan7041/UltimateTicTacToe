package com.example.ultimatetictactoe.artificialintelligence;

import com.example.ultimatetictactoe.UltimateTicTacToeBackEndGame;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArtificialIntelligenceEngine {
    private UltimateTicTacToeBackEndGame game;
    private Set<Node> clickableMiniGrids;

    public ArtificialIntelligenceEngine(UltimateTicTacToeBackEndGame game, Set<Node> clickableMiniGrids){
        this.game = game;
        this.clickableMiniGrids = clickableMiniGrids;
    }


    public BestMove getBestAIMove() throws Exception {
        int depth = 2;
        return minimax(game, depth, false);
    }

    private BestMove minimax(UltimateTicTacToeBackEndGame game, int depth, boolean maximizingPlayer) throws Exception {
        if (depth == 0 || game.isGameOver()) {
            return new BestMove(evaluate(game), null);
        }

        BestMove bestMove = new BestMove();

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : getAvailableMoves()) {
                game.recordMove(move.getButton(), move.getMiniGrid());
                BestMove m = minimax(game, depth - 1, false);
                if (m.getScore() > maxEval) {
                    maxEval = m.getScore();
                    bestMove.setScore(maxEval);
                    bestMove.setMove(move);
                }
                game.undoMove(move.getButton(), move.getMiniGrid());
            }
            return bestMove;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : getAvailableMoves()) {
                game.recordMove(move.getButton(), move.getMiniGrid());
                BestMove m = minimax(game, depth - 1, true);
                if (m.getScore() < minEval) {
                    minEval = m.getScore();
                    bestMove.setScore(minEval);
                    bestMove.setMove(move);
                }
                game.undoMove(move.getButton(), move.getMiniGrid());
            }
            return bestMove;
        }
    }

    private int evaluate(UltimateTicTacToeBackEndGame game) {
        return 0;
    }

    private List<Move> getAvailableMoves(){
        List<Move> availableMoves = new ArrayList<>();
        for(Node miniGridNode : clickableMiniGrids){
            GridPane miniGrid = (GridPane) miniGridNode;
            for(Node buttonNode : miniGrid.getChildren()){
                Button button = (Button) buttonNode;
                if(button.getGraphic() == null){
                    availableMoves.add(new Move(button, miniGrid));
                }
            }
        }
        return availableMoves;
    }
}
