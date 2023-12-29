package com.example.ultimatetictactoe;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.Set;

public class LastMove {
    private final Button button;
    private final GridPane miniGrid;
    private final Set<Node> clickableMiniGrids;

    public LastMove(Button button, GridPane miniGrid, Set<Node> clickableMiniGrids) {
        this.button = button;
        this.miniGrid = miniGrid;
        this.clickableMiniGrids = clickableMiniGrids;
    }

    public Button getButton() {
        return button;
    }

    public GridPane getMiniGrid() {
        return miniGrid;
    }

    public Set<Node> getClickableMiniGrids() {
        return clickableMiniGrids;
    }
}
