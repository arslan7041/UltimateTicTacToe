package com.example.ultimatetictactoe;

public class Player {
    private final int id;
    private final String labelValue;
    private final String labelColor;
    private final int labelSize;

    public int wins = 0;
    public boolean wonGame = false;
    public int undos = 1;

    public Player(int id, String label, String color, int size) {
        this.id = id;
        this.labelValue = label;
        this.labelColor = color;
        this.labelSize = size;
    }

    public int getId() {
        return id;
    }

    public String getLabelValue() {
        return labelValue;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public int getLabelSize() {
        return labelSize;
    }

    public void resetPlayer(){
        wins = 0;
        wonGame = false;
    }
}
