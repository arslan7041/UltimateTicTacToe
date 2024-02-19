package com.example.ultimatetictactoe.artificialintelligence;

import com.example.ultimatetictactoe.UltimateTicTacToeBackEndGame;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArtificialIntelligenceEngine {
    private GameState game;
    private Set<Node> clickableMiniGrids;

    public BestMove getBestAIMove(UltimateTicTacToeBackEndGame game, Set<Node> clickableMiniGrids) throws Exception {
        ModelMapper modelMapper = ModelMapperSingleton.getInstance();
        this.game = modelMapper.map(game, GameState.class);
        this.clickableMiniGrids = new HashSet<>(clickableMiniGrids);

        int depth = 4;
        return minimax(game.getGrid(), depth, false);
    }

    private BestMove minimax(int[][][][] grid, int depth, boolean maximizingPlayer) throws Exception {
        if (depth == 0 || isGameOver()) {
            return new BestMove(evaluate(grid, maximizingPlayer), null);
        }

        BestMove bestMove = new BestMove();

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : getAvailableMoves()) {
                recordTempMove(grid, move.getButton(), move.getMiniGrid(), maximizingPlayer);
                BestMove m = minimax(grid, depth - 1, false);
                if (m.getScore() > maxEval) {
                    maxEval = m.getScore();
                    bestMove.setScore(maxEval);
                    bestMove.setMove(move);
                }
                undoTempMove(grid, move.getButton(), move.getMiniGrid());
            }
            return bestMove;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : getAvailableMoves()) {
                recordTempMove(grid, move.getButton(), move.getMiniGrid(), maximizingPlayer);
                BestMove m = minimax(grid, depth - 1, true);
                if (m.getScore() < minEval) {
                    minEval = m.getScore();
                    bestMove.setScore(minEval);
                    bestMove.setMove(move);
                }
                undoTempMove(grid, move.getButton(), move.getMiniGrid());
            }
            return bestMove;
        }
    }

    private void recordTempMove(int[][][][] grid, Button button, GridPane miniGrid, boolean player1Turn) {
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);

        grid[i][j][row][col] =  player1Turn ? 1 : 2;
    }

    private void undoTempMove(int[][][][] grid, Button button, GridPane miniGrid) {
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);

        grid[i][j][row][col] = 0;
    }

    private int evaluate(int[][][][] grid, boolean player1Turn) throws Exception {
        return heur1(grid, player1Turn);
    }

    private int heur1(int[][][][] grid, boolean player1Turn) throws Exception {
        if(game.isTie() ){
            return 0;
        }else if(game.getPlayer1().hasWonGame()){ // player2 is AI
            return 10000;
        }else if(game.getPlayer2().hasWonGame()){
            return 10000; // tie
        }else{
            return game.getPlayer1().getMiniGridWins() - game.getPlayer2().getMiniGridWins();
        }
    }

    private boolean isGameOver(){
        return game.getPlayer1().hasWonGame() || game.getPlayer2().hasWonGame() || game.isTie();
    }


    private boolean gameWon(int[][][][] grid) {
        return checkMainGridRows(grid) || checkMainGridColumns(grid) || checkMainGridDiagonals(grid);
    }

    private boolean isGameComplete(int[][][][] grid){
        for (int i = 0; i < 3; i++) {
            for (int row = 0; row < 3; row++) {
                for (int j = 0; j < 3; j++) {
                    for (int col = 0; col < 3; col++) {
                        if(grid[i][j][row][col] == 0){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean checkMainGridRows(int[][][][] grid) {
        for (int i = 0; i < 3; i++) {
            if ( wonMiniGrid(grid[i][0]) && wonMiniGrid(grid[i][1]) && wonMiniGrid(grid[i][2]) ) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMainGridColumns(int[][][][] grid) {
        for (int i = 0; i < 3; i++) {
            if ( wonMiniGrid(grid[0][i]) && wonMiniGrid(grid[1][i]) && wonMiniGrid(grid[2][i]) ) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMainGridDiagonals(int[][][][] grid) {
        if ( wonMiniGrid(grid[0][0]) && wonMiniGrid(grid[1][1]) && wonMiniGrid(grid[2][2]) ){
            return true;
        }
        if ( wonMiniGrid(grid[0][2]) && wonMiniGrid(grid[1][1]) && wonMiniGrid(grid[2][0]) ){
            return true;
        }
        return false;
    }

    private boolean wonMiniGrid(int[][] miniGrid){
        return checkMiniGridRows(miniGrid) || checkMiniGridColumns(miniGrid) || checkMiniGridDiagonals(miniGrid);
    }

    private boolean checkMiniGridRows(int[][] miniGrid){
        for (int i = 0; i < 3; i++) {
            if (miniGrid[i][0] == miniGrid[i][1] && miniGrid[i][1] == miniGrid[i][2] && miniGrid[i][0] != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMiniGridColumns(int[][] miniGrid){
        for (int i = 0; i < 3; i++) {
            if (miniGrid[0][i] == miniGrid[1][i] && miniGrid[1][i] == miniGrid[2][i] && miniGrid[0][i] != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMiniGridDiagonals(int[][] miniGrid){
        if (miniGrid[0][0] == miniGrid[1][1] && miniGrid[1][1] == miniGrid[2][2] && miniGrid[0][0] != 0){
            return true;
        }
        if (miniGrid[0][2] == miniGrid[1][1] && miniGrid[1][1] == miniGrid[2][0] && miniGrid[0][2] != 0){
            return true;
        }
        return false;
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
