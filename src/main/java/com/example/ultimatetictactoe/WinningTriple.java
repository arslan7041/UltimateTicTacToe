package com.example.ultimatetictactoe;

import java.util.ArrayList;
import java.util.List;

public class WinningTriple {
    private List<Coordinate> coordinates;

    public WinningTriple() {
        this.coordinates = new ArrayList<>();
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public WinningTriple addCoordinate(int x, int y) throws Exception {
        if(coordinates.size() == 3){
            throw new Exception("WinningTriple object already has 3 Coordinates");
        }
        coordinates.add(new Coordinate(x, y));
        return this;
    }

    public static class Coordinate {
        private int x;
        private int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
