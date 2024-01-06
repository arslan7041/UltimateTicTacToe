package com.example.ultimatetictactoe;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

import static com.example.ultimatetictactoe.Constants.*;

public class UltimateTicTacToeBackEndGame {
    private final Player player1;
    private final Player player2;
    public boolean player1Turn = true;
    public boolean isTie = false;

    private int[][][][] grid;
    private int[][] miniGridWinsBoard;
    private List<WinningTriple> lastWinningCoordinates;


    public UltimateTicTacToeBackEndGame() {
        grid = new int[3][3][3][3];
        miniGridWinsBoard = new int[3][3];
        lastWinningCoordinates =  new ArrayList<>();
        player1 = new Player(1, PLAYER1_LABEL, PLAYER1_LABEL_COLOR, PLAYER1_LABEL_SIZE);
        player2 = new Player(2, PLAYER2_LABEL, PLAYER2_LABEL_COLOR, PLAYER2_LABEL_SIZE);
    }

    public void resetBackEndGame(){
        grid = new int[3][3][3][3];
        miniGridWinsBoard = new int[3][3];
        lastWinningCoordinates = new ArrayList<>();
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

    public boolean checkMiniGridForWin(GridPane miniGrid) throws Exception {
        int i = GridPane.getRowIndex(miniGrid);
        int j = GridPane.getColumnIndex(miniGrid);

        if (foundWinningRowsColumnsDiagonals(grid[i][j])) {
            if (player1Turn) {
                player1.wins++;
                miniGridWinsBoard[i][j] = 1;
            } else {
                player2.wins++;
                miniGridWinsBoard[i][j] = 2;
            }
            return true;
        }
        return false;
    }

    public boolean checkGameForWinOrTie() throws Exception {
        if (foundWinningRowsColumnsDiagonals(miniGridWinsBoard)) {
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
        return player1.wonGame || player2.wonGame || isTie;
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
            player1.wins--;
        } else if(miniGridWinsBoard[i][j] == 2) {
            player2.wins--;
        }
        miniGridWinsBoard[i][j] = 0;
    }

//    public List<List<Integer>> getWinningCoordinates(){
//        List<List<Integer>> winningCoordinates = new ArrayList<>();
//
//        // check rows and columns
//        for (int i = 0; i < 3; i++) {
//            if (miniGridWinsBoard[i][0] == miniGridWinsBoard[i][1] && miniGridWinsBoard[i][1] == miniGridWinsBoard[i][2] && miniGridWinsBoard[i][0] != 0 && miniGridWinsBoard[i][0] != -1) {
//                winningCoordinates.add(Arrays.asList(i, 0));
//                winningCoordinates.add(Arrays.asList(i, 1));
//                winningCoordinates.add(Arrays.asList(i, 2));
//            }
//            if (miniGridWinsBoard[0][i] == miniGridWinsBoard[1][i] && miniGridWinsBoard[1][i] == miniGridWinsBoard[2][i] && miniGridWinsBoard[0][i] != 0 && miniGridWinsBoard[0][i] != -1) {
//                winningCoordinates.add(Arrays.asList(0, i));
//                winningCoordinates.add(Arrays.asList(1, i));
//                winningCoordinates.add(Arrays.asList(2, i));
//            }
//        }
//
//        // check diagonals
//        if(miniGridWinsBoard[0][0] == miniGridWinsBoard[1][1] && miniGridWinsBoard[1][1] == miniGridWinsBoard[2][2] && miniGridWinsBoard[0][0] != 0 && miniGridWinsBoard[0][0] != -1){
//            winningCoordinates.add(Arrays.asList(0, 0));
//            winningCoordinates.add(Arrays.asList(1, 1));
//            winningCoordinates.add(Arrays.asList(2, 2));
//        }
//        if(miniGridWinsBoard[0][2] == miniGridWinsBoard[1][1] && miniGridWinsBoard[1][1] == miniGridWinsBoard[2][0] && miniGridWinsBoard[0][2] != 0 && miniGridWinsBoard[0][2] != -1){
//            winningCoordinates.add(Arrays.asList(0, 2));
//            winningCoordinates.add(Arrays.asList(1, 1));
//            winningCoordinates.add(Arrays.asList(2, 0));
//        }
//
//        return winningCoordinates;
//    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public List<WinningTriple> getLastWinningCoordinates(){
        return lastWinningCoordinates;
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

        System.out.println("=================================================");
    }
}
