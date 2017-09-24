package com.titanium.evolve.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Taylor on 8/23/2016.
 */
public class SudokuPuzzle {

    private final List<Square> unknownValues;


    final int[][] puzzle;

    public SudokuPuzzle(final int[][] data) {
        unknownValues = new ArrayList<>();
        puzzle = new int[9][];
        for (int i = 0; i < 9; i++) {
            puzzle[i] = new int[9];
            for (int j = 0; j < 9; j++) {
                int val = data[i][j];
                if (val == 0) {
                    unknownValues.add(new Square(i, j, val));
                } else {
                    puzzle[i][j] = val;
                }
            }
        }
    }

    public List<Square> getUnknownValues() {
        return this.unknownValues;
    }

    private void applyGuessesToPuzzle(final List<Square> guesses) {
        for (final Square guess : guesses) {
            puzzle[guess.getRow()][guess.getCol()] = guess.getValue();
        }
    }

    private void clearGuessesFromPuzzle(final List<Square> guesses) {
        for (final Square guess : guesses) {
            puzzle[guess.getRow()][guess.getCol()] = 0;
        }
    }

    public int verify(final List<Square> guesses) {

        applyGuessesToPuzzle(guesses);

        int numCorrect = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (verify(row, col)) {
                    numCorrect++;
                }
            }
        }

        clearGuessesFromPuzzle(guesses);

        return numCorrect;
    }

    private boolean verify(int row, int col) {
        if (puzzle[row][col] == 0) {
            return false;
        }

        for (int i = 0; i < 9; i++) {
            if ((i != col && puzzle[row][i] == puzzle[row][col]) ||
                    (i != row && puzzle[i][col] == puzzle[row][col])) {
                return false;
            }
        }

        for (int i = (row / 3) * 3; i < (1 + (row / 3)) * 3; i++) {
            for (int j = (col / 3) * 3; j < (1 + (col / 3)) * 3; j++) {
                if ((i != row || j != col) &&
                        puzzle[i][j] == puzzle[row][col]) {
                    return false;
                }
            }
        }

        return true;
    }
}
