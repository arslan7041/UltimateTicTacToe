package com.example.ultimatetictactoe.artificialintelligence;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import lombok.Data;

@Data
public class Move {
    private final Button button;
    private final GridPane miniGrid;
}
