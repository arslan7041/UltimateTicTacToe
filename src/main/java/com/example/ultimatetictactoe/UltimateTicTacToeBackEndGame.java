package com.example.ultimatetictactoe;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.example.ultimatetictactoe.Constants.*;

@Data
public class UltimateTicTacToeBackEndGame {
    private final Player player1;
    private final Player player2;
    private int[][][][] grid;
    private int[][] miniGridWinsBoard;
    private List<WinningTriple> lastWinningCoordinates;
    private boolean player1Turn;
    private boolean isTie;

    public UltimateTicTacToeBackEndGame() {
        player1 = new Player(1, PLAYER1_LABEL, PLAYER1_LABEL_COLOR, PLAYER1_LABEL_SIZE);
        player2 = new Player(2, PLAYER2_LABEL, PLAYER2_LABEL_COLOR, PLAYER2_LABEL_SIZE);
        grid = new int[3][3][3][3];
        miniGridWinsBoard = new int[3][3];
        lastWinningCoordinates =  new ArrayList<>();
        player1Turn = true;
        isTie = false;

    }

    public void resetBackEndGame(){
        player1.resetPlayer();
        player2.resetPlayer();
        grid = new int[3][3][3][3];
        miniGridWinsBoard = new int[3][3];
        lastWinningCoordinates = new ArrayList<>();
        player1Turn = true;
        isTie = false;
    }

    public void recordMove(Button button, GridPane miniGrid){
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);

        grid[i][j][row][col] = player1Turn ? 1 : 2;
    }

    public boolean checkMiniGridForWin(GridPane miniGrid) throws Exception {
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

    public boolean isGameOver() throws Exception {
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

    public boolean isGridComplete(GridPane miniGrid){
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

    private boolean foundWinningRowsColumnsDiagonals(int[][] grid) throws Exception {
        lastWinningCoordinates.clear();
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

    private boolean checkGridRows(int[][] grid) throws Exception {
        for (int i = 0; i < 3; i++) {
            if (grid[i][0] == grid[i][1] && grid[i][1] == grid[i][2] && grid[i][0] != 0 && grid[i][0] != -1) {
                lastWinningCoordinates.add(new WinningTriple()
                        .addCoordinate(i, 0)
                        .addCoordinate(i, 1)
                        .addCoordinate(i, 2)
                );
                return true;
            }
        }
        return false;
    }

    private boolean checkGridColumns(int[][] grid) throws Exception {
        for (int i = 0; i < 3; i++) {
            if (grid[0][i] == grid[1][i] && grid[1][i] == grid[2][i] && grid[0][i] != 0 && grid[0][i] != -1) {
                lastWinningCoordinates.add(new WinningTriple()
                        .addCoordinate(0, i)
                        .addCoordinate(1, i)
                        .addCoordinate(2, i)
                );
                return true;
            }
        }
        return false;
    }

    private boolean checkGridDiagonals(int[][] grid) throws Exception {
        boolean diagonalFound = false;
        if (grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2] && grid[0][0] != 0 && grid[0][0] != -1){
            lastWinningCoordinates.add(new WinningTriple()
                    .addCoordinate(0, 0)
                    .addCoordinate(1, 1)
                    .addCoordinate(2, 2)
            );
            diagonalFound = true;
        }
        if (grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0] && grid[0][2] != 0 && grid[0][2] != -1){
            lastWinningCoordinates.add(new WinningTriple()
                    .addCoordinate(2, 0)
                    .addCoordinate(1, 1)
                    .addCoordinate(0, 2)
        );
            diagonalFound = true;
        }
        return diagonalFound;
    }

    public void undoMove(Button button, GridPane miniGrid){
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);

        grid[i][j][row][col] = 0;
    }

    public void undoMiniGridCompletionAndWin(GridPane miniGrid){
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);

        if (miniGridWinsBoard[i][j] == 1) {
            player1.decrementMiniGridWins();
        } else if(miniGridWinsBoard[i][j] == 2) {
            player2.decrementMiniGridWins();
        }
        miniGridWinsBoard[i][j] = 0;
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
