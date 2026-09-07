package com.example.ultimatetictactoe.artificialintelligence;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Move {
    private Coordinates miniGrid;
    private Coordinates button;
}
