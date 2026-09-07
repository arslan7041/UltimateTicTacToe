package com.example.ultimatetictactoe.artificialintelligence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BestMove {
    private int score;
    private Move move;
}
