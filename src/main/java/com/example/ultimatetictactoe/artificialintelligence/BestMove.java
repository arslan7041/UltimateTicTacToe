package com.example.ultimatetictactoe.artificialintelligence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BestMove {
    private int score;
    private Move move;
}
