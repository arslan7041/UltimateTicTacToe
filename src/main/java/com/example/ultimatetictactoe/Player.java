package com.example.ultimatetictactoe;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class Player {
    private final int id;
    private final String labelValue;
    private final String labelColor;
    private final int labelSize;
    private int miniGridWins;
    @Accessors(fluent = true)
    private boolean hasWonGame;

    public int undos = 1;

    public Player(int id, String label, String color, int size) {
        this.id = id;
        this.labelValue = label;
        this.labelColor = color;
        this.labelSize = size;
        this.miniGridWins = 0;
        this.hasWonGame = false;
    }

    public void resetPlayer(){
        this.miniGridWins = 0;
        this.hasWonGame = false;
        this.undos = 1;
    }

    public void makeMove(Button button){
        Label buttonLabel = new Label();
        buttonLabel.setText(this.getLabelValue());
        buttonLabel.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: %dem;", this.getLabelColor(), this.getLabelSize()));
        button.setGraphic(buttonLabel);
    }

    public void incrementMiniGridWins(){
        miniGridWins++;
    }

    public void decrementMiniGridWins(){
        miniGridWins--;
    }
}
