package com.example.ultimatetictactoe;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import static com.example.ultimatetictactoe.Constants.*;

public class UltimateTicTaceToeGame {
    private final Player player1;
    private final Player player2;
    public boolean player1Turn = true;
    private int[][][][] grid;
    private int[][] miniGridWinsBoard;
    public boolean isTie = false;

    public UltimateTicTaceToeGame() {
        player1 = new Player(1, PLAYER1_LABEL, PLAYER1_LABEL_COLOR, PLAYER1_LABEL_SIZE);
        player2 = new Player(2, PLAYER2_LABEL, PLAYER2_LABEL_COLOR, PLAYER2_LABEL_SIZE);

        grid = new int[3][3][][];
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                grid[i][j] = new int[3][3];
            }
        }
        miniGridWinsBoard = new int[3][3];
    }

    public void resetBackEndGame(){
        grid = new int[3][3][][];
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                grid[i][j] = new int[3][3];
            }
        }
        miniGridWinsBoard = new int[3][3];
        isTie = false;
        player1.resetPlayer();
        player2.resetPlayer();
        player1Turn = true;
    }

    public void recordMove(Button button, GridPane miniGrid){
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);

        grid[i][j][row][col] = player1Turn ? 1 : 2;
    }

    public boolean checkMiniGridForWin(GridPane miniGrid){
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);

        int[][] miniGridArr = grid[i][j];
        if (checkGridRows(miniGridArr) || checkGridColumns(miniGridArr) || checkGridDiagonals(miniGridArr)) {
            if (player1Turn) {
                getPlayer1().wins++;
                miniGridWinsBoard[i][j] = 1;
            } else {
                getPlayer2().wins++;
                miniGridWinsBoard[i][j] = 2;
            }
            return true;
        }
        return false;
    }

    public void checkGameForWin(){
        if (checkGridRows(miniGridWinsBoard) || checkGridColumns(miniGridWinsBoard) || checkGridDiagonals(miniGridWinsBoard)) {
            if (player1Turn) {
                player1.wonGame = true;
            } else {
                player2.wonGame = true;
            }
        } else if(isGridComplete(miniGridWinsBoard)){
            if(player1.wins == player2.wins){
                isTie = true;
            }else if(player1.wins > player2.wins){
                player1.wonGame = true;
            }else{
                player2.wonGame = true;
            }
        }
    }

    public boolean isGridComplete(GridPane grid){
        int i = GridPane.getRowIndex(grid);
        int j = GridPane.getColumnIndex(grid);

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
        return (grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2] && grid[0][0] != 0 && grid[0][0] != -1) ||
                (grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0] && grid[0][2] != 0 && grid[0][2] != -1);
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
}
