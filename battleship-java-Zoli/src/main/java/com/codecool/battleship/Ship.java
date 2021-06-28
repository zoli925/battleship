package com.codecool.battleship;

public class Ship {
    private int size;
    private int[][] coordinate;


    public Ship(int size) {
        this.size = size;
        this.coordinate = new int[size][2];// annyi koordináta párja van amekkora a mérete, tömb kinézete pl: [[1, 1], [1, 2]]
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int[][] getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(int[][] coordinate) {
        this.coordinate = coordinate;
    }
}
