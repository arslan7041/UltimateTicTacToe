package com.example.ultimatetictactoe.artificialintelligence;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class PlayerState {
    private int miniGridWins;
    @Accessors(fluent = true)
    private boolean hasWonGame;

    public void incrementMiniGridWins(){
        miniGridWins++;
    }

    public void decrementMiniGridWins(){
        miniGridWins--;
    }
}
