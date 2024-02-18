package com.example.ultimatetictactoe.artificialintelligence;

import lombok.Data;

@Data
public class GameState {
    private final PlayerState player1;
    private final PlayerState player2;
    private int[][][][] grid;
    private int[][] miniGridWinsBoard;
    private boolean player1Turn;
    private boolean isTie;
}
