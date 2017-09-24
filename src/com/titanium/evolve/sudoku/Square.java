package com.titanium.evolve.sudoku;

/**
 * Created by Taylor on 8/23/2016.
 */
public class Square {
    public final int row;
    public final int col;
    public int value;

    public Square(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public Square(final Square square) {
        this.row = square.getRow();
        this.col = square.getCol();
        this.value = square.getValue();
    }


    public void setVal(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
