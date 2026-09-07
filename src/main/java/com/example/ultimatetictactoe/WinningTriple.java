package com.example.ultimatetictactoe;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class WinningTriple {
    @Getter
    private final List<Coordinate> coordinates;

    public WinningTriple() {
        this.coordinates = new ArrayList<>();
    }

    public WinningTriple addCoordinate(int x, int y) throws Exception {
        if(coordinates.size() == 3){
            throw new Exception("Attempt made to add more than 3 Coordinates into WinningTriple object");
        }
        coordinates.add(new Coordinate(x, y));
        return this;
    }

    @Data
    public static class Coordinate {
        private final int x;
        private final int y;
    }
}
