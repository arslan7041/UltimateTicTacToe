package com.example.ultimatetictactoe;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import lombok.Data;

import java.util.Set;

@Data
public class LastMove {
    private final Button button;
    private final GridPane miniGrid;
    private final Set<Node> clickableMiniGrids;
}
