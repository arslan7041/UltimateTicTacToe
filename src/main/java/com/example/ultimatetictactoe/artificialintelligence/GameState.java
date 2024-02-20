package com.example.ultimatetictactoe.artificialintelligence;

import com.example.ultimatetictactoe.GameUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class GameState {
    private PlayerState player1;
    private PlayerState player2;
    private int[][][][] grid;
    private int[][] miniGridWinsBoard;
    private boolean player1Turn;
    private boolean isTie;
    private Set<Node> clickableMiniGrids;
    private GridPane mainGrid;

    public void simulateTurn(Button button, GridPane miniGrid){
        recordMove(button, miniGrid);
        updateMiniGridIfWonOrTie(miniGrid);
        clickableMiniGrids.clear();
        boolean isGameOver = isGameOver();
        setPlayer1Turn(!isPlayer1Turn());
        if(!isGameOver){
            updateClickableMiniGrids(button);
        }
        printUltimateTicTacToeGrid();
    }

    public void undoTurn(Button button, GridPane miniGrid){
        undoMove(button, miniGrid);
        undoMiniGridWonOrTie(miniGrid);
    }

    private void recordMove(Button button, GridPane miniGrid){
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);

        grid[i][j][row][col] = player1Turn ? 1 : 2;
    }

    private void undoMove(Button button, GridPane miniGrid) {
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);

        grid[i][j][row][col] = 0;
    }

    private void updateMiniGridIfWonOrTie(GridPane miniGrid){
        boolean gridWon = checkMiniGridForWin(miniGrid);
        if(!gridWon){
            isGridComplete(miniGrid);
        }
    }

    private void undoMiniGridWonOrTie(GridPane miniGrid) {
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);

        if(miniGridWinsBoard[i][j] == 1){
            player1.decrementMiniGridWins();
        }else if(miniGridWinsBoard[i][j] == 2){
            player2.decrementMiniGridWins();
        }

        miniGridWinsBoard[i][j] = 0;
    }

    private boolean checkMiniGridForWin(GridPane miniGrid) {
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);

        if (foundWinningRowsColumnsDiagonals(grid[i][j])) {
            if (player1Turn) {
                player1.incrementMiniGridWins();
                miniGridWinsBoard[i][j] = 1;
            } else {
                player2.incrementMiniGridWins();
                miniGridWinsBoard[i][j] = 2;
            }
            return true;
        }
        return false;
    }

    private boolean isGridComplete(GridPane miniGrid){
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);

        if(isGridComplete(this.grid[i][j])){
            miniGridWinsBoard[i][j] = -1;
            return true;
        }
        return false;
    }

    private boolean isGridComplete (int[][] grid){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(grid[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean foundWinningRowsColumnsDiagonals(int[][] grid) {
        boolean foundWin = false;
        if(checkGridRows(grid)){
            foundWin = true;
        }
        if(checkGridColumns(grid)){
            foundWin = true;
        }
        if(checkGridDiagonals(grid)){
            foundWin = true;
        }
        return foundWin;
    }

    private boolean checkGridRows(int[][] grid) {
        for (int i = 0; i < 3; i++) {
            if (grid[i][0] == grid[i][1] && grid[i][1] == grid[i][2] && grid[i][0] != 0 && grid[i][0] != -1) {
                return true;
            }
        }
        return false;
    }

    private boolean checkGridColumns(int[][] grid) {
        for (int i = 0; i < 3; i++) {
            if (grid[0][i] == grid[1][i] && grid[1][i] == grid[2][i] && grid[0][i] != 0 && grid[0][i] != -1) {
                return true;
            }
        }
        return false;
    }

    private boolean checkGridDiagonals(int[][] grid) {
        boolean diagonalFound = false;
        if (grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2] && grid[0][0] != 0 && grid[0][0] != -1){
            diagonalFound = true;
        }
        if (grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0] && grid[0][2] != 0 && grid[0][2] != -1){
            diagonalFound = true;
        }
        return diagonalFound;
    }

    public boolean isGameOver() {
        if (foundWinningRowsColumnsDiagonals(miniGridWinsBoard)) {
            if (player1Turn) {
                player1.hasWonGame(true);
            } else {
                player2.hasWonGame(true);
            }
        } else if(isGridComplete(miniGridWinsBoard)){
            if(player1.getMiniGridWins() == player2.getMiniGridWins()){
                isTie = true;
            }else if(player1.getMiniGridWins() > player2.getMiniGridWins()){
                player1.hasWonGame(true);
            }else{
                player2.hasWonGame(true);
            }
        }
        return player1.hasWonGame() || player2.hasWonGame() || isTie;
    }

    private void updateClickableMiniGrids(Button button) {
        int buttonX = GridPane.getRowIndex(button);
        int buttonY = GridPane.getColumnIndex(button);

        if(miniGridWinsBoard[buttonX][buttonY] == 0) { // if minigrid not won or tie (i.e. not disabled)
            GridPane nextMiniGrid = GameUtils.getGridPaneGivenIndices(mainGrid, buttonX, buttonY);
            clickableMiniGrids.add(nextMiniGrid);
        }else{
            for(int i = 0; i < miniGridWinsBoard.length; i++){
                for(int j = 0; j < miniGridWinsBoard[i].length; j++){
                    if(miniGridWinsBoard[i][j] == 0){
                        clickableMiniGrids.add(GameUtils.getGridPaneGivenIndices(mainGrid, i, j));
                    }
                }
            }
        }
    }

    public List<Move> getAvailableMoves(){
        List<Move> availableMoves = new ArrayList<>();
        for(Node miniGridNode : clickableMiniGrids){
            GridPane miniGrid = (GridPane) miniGridNode;
            for(Node buttonNode : miniGrid.getChildren()){
                Button button = (Button) buttonNode;
                int miniGridX = GridPane.getRowIndex(miniGrid);
                int miniGridY = GridPane.getColumnIndex(miniGridNode);
                int buttonX = GridPane.getRowIndex(button);
                int buttonY = GridPane.getColumnIndex(button);

                if(grid[miniGridX][miniGridY][buttonX][buttonY] == 0){
                    availableMoves.add(new Move(button, miniGrid));
                }
            }
        }
        return availableMoves;
    }

    public void printUltimateTicTacToeGrid() {
        for (int i = 0; i < 3; i++) {
            for (int row = 0; row < 3; row++) {
                for (int j = 0; j < 3; j++) {
                    for (int col = 0; col < 3; col++) {
                        System.out.print(grid[i][j][row][col] + " ");
                    }
                    System.out.print("  ");
                }
                System.out.println();
            }
            System.out.println();
        }

        System.out.println("Player 1 mini grid wins: " + getPlayer1().getMiniGridWins());
        System.out.println("Player 2 mini grid wins: " + getPlayer2().getMiniGridWins());
        System.out.println("=================================================");
    }
}
