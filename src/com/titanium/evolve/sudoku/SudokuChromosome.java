package com.titanium.evolve.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Taylor on 8/23/2016.
 */
public class SudokuChromosome {

    private final Random random;
    private final List<Square> genes;
    private int rank = 0;

    public SudokuChromosome(final List<Square> genes) {
        random = new Random(System.nanoTime());
        this.genes = new ArrayList<>();
        for (final Square square : genes) {
            this.genes.add(new Square(square));
        }
    }

    public void mutate(final int percent) {
        if (percent <= 0) {
            return;
        } else if (percent >= 100) {
            for (Square square : genes) {
                setRandomInt(square);
            }
            return;
        }

        int numMutations = (genes.size() * percent) / 100;
        for (int i = 0; i < numMutations; i++) {
            setRandomInt(genes.get(random.nextInt(genes.size())));
        }
    }

    public void pointCrossover(final SudokuChromosome chromosome) {
        final List<Square> tempGeneList = new ArrayList<>(genes.size());
        int randPoint = random.nextInt(genes.size());
        for (Square square : genes.subList(0, randPoint)) {
            tempGeneList.add(new Square(square));
        }
        for (final Square square : chromosome.getGenes().subList(randPoint, genes.size())) {
            tempGeneList.add(new Square(square));
        }
        genes.clear();
        genes.addAll(tempGeneList);
    }

    public void uniformCrossover(final SudokuChromosome chromosome) {
        final List<Square> tempGeneList = new ArrayList<>(genes.size());
        for (int i = 0; i < genes.size(); i++) {
            if (random.nextInt(2) == 0) {
                tempGeneList.add(new Square(genes.get(i)));
            } else {
                tempGeneList.add(new Square(chromosome.getGenes().get(i)));
            }
        }
        genes.clear();
        genes.addAll(tempGeneList);
    }

    private void setRandomInt(final Square square) {
        square.setVal(random.nextInt(9) + 1);
    }

    public List<Square> getGenes() {
        return genes;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public Integer getRank() {
        return rank;
    }
}
