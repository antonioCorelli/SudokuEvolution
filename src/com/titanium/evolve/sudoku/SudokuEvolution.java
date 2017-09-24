package com.titanium.evolve.sudoku;

import java.util.*;

/**
 * Created by Taylor on 8/22/2016.
 */
public class SudokuEvolution {
    final static int NUM_SPECIES = 10;
    final static int NUM_SUPER_SPECIES = 10;
    final static int NUM_SUPER_SUPER_SPECIES = 10;

    final static int NUM_TO_DECLARE_SUPER = 1000;

    public static void main(String[] args) {

        final SudokuPuzzle puzzle = new SudokuPuzzle(new int[][]{
                {0,0,1,  0,0,9,  4,0,7},
                {0,9,0,  7,1,0,  8,2,0},
                {0,0,0,  0,6,0,  0,1,9},

                {0,0,0,  1,8,0,  9,0,4},
                {8,0,9,  2,0,0,  6,0,0},
                {4,0,3,  0,9,0,  0,0,2},

                {9,0,0,  6,7,0,  0,3,0},
                {0,6,0,  0,3,8,  2,0,0},
                {0,0,0,  4,0,2,  7,0,0}
        });

        final List<Square> unknownValues = puzzle.getUnknownValues();

        final List<SudokuChromosome> chromosomes = new ArrayList<>();
        final Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < NUM_SPECIES; i++) {
            final SudokuChromosome chromosome = new SudokuChromosome(puzzle.getUnknownValues());
            chromosome.mutate(100);
            chromosomes.add(chromosome);
        }

        evolve(puzzle, chromosomes);

        System.out.println("Finnished");
    }

    private static void evolve(final SudokuPuzzle puzzle, List<SudokuChromosome> chromosomes) {
        final List<SudokuChromosome> superSuperSpecies = new ArrayList<>();

        while (superSuperSpecies.size() < NUM_SUPER_SUPER_SPECIES) {

            final List<SudokuChromosome> superChromosomes = new ArrayList<>();
            int curGeneration = 0;
            int bestSpecies = 0;
            int generationsWithoutIncrease = 0;

            while (superChromosomes.size() < NUM_SUPER_SPECIES) {

                curGeneration++;

                rankAndSortSpecies(puzzle, chromosomes);


                if (bestSpecies < chromosomes.get(0).getRank()) {
                    bestSpecies = chromosomes.get(0).getRank();
                    generationsWithoutIncrease = 0;
                } else {
                    generationsWithoutIncrease++;
                }

                if (generationsWithoutIncrease >= NUM_TO_DECLARE_SUPER) {
                    final SudokuChromosome superChromosome = new SudokuChromosome(chromosomes.get(0).getGenes());
                    superChromosome.setRank(chromosomes.get(0).getRank());
                    superChromosomes.add(superChromosome);
                    System.out.println("Super Species " + superChromosomes.size() + "/" + NUM_SUPER_SPECIES + " selected with rank ( " + chromosomes.get(0).getRank() + " ) at generation (" + curGeneration + ")");
                    curGeneration = 0;
                    bestSpecies = 0;
                    generationsWithoutIncrease = 0;
                    for (final SudokuChromosome chromosome : chromosomes) {
                        chromosome.mutate(100);
                        continue;
                    }
                }

                crossSpecies(chromosomes);
                mutateSpecies(chromosomes, 0, 100);
            }

            for (int i = 0; i < 100000; i++) {
                rankAndSortSpecies(puzzle, superChromosomes);

                if (i % 1000 == 0) {
                    System.out.println("Generation (" + i + ") of super species with rank " + superChromosomes.get(0).getRank());
                }

                crossSpecies(superChromosomes);
                mutateSpecies(superChromosomes, 0, 50);
            }

            System.out.println("adding to super super species a species with rank ( " + superChromosomes.get(0).getRank() + ")");
            final SudokuChromosome superSuperChromosome = new SudokuChromosome(superChromosomes.get(0).getGenes());
            superSuperChromosome.setRank(superChromosomes.get(0).getRank());
            superSuperSpecies.add(superSuperChromosome);
        }

        for (int i = 0; i < 100000; i++) {
            rankAndSortSpecies(puzzle, superSuperSpecies);

            if (i % 1000 == 0) {
                System.out.println("Generation (" + i + ") of super species with rank " + superSuperSpecies.get(0).getRank());
            }

            crossSpecies(superSuperSpecies);
            mutateSpecies(superSuperSpecies, 0, 10);
        }
    }

    /**
     * Will apply an evenly distributed mutation percent based on percentToMutate on the chromosomes \n
     * chromosomes.get(0) will have startPercent mutations and chromosomes.get(chromosomes.size() - 1) will have endPercent mutations
     * @param chromosomes chromosomes to apply the evenly distributed mutations to
     * @param startPercent starting portion of the chromosomes to mutate
     * @param endPercent ending portion of the chromosomes to mutate
     */
    private static void mutateSpecies(final List<SudokuChromosome> chromosomes, int startPercent, int endPercent) {
        if (startPercent < 0 || startPercent > 100 || endPercent < 0 || endPercent > 100 || endPercent < startPercent || chromosomes == null) {
            throw new IllegalArgumentException(
                    "if (startPercent < 0 || startPercent > 100 || endPercent < 0 || endPercent > 100" +
                            " || endPercent < startPercent || chromosomes == null)\n" +
                            "inputs(chromosomes:" + String.valueOf(chromosomes) +
                            " startPercent:" + String.valueOf(startPercent) +
                            " endPercent: " + String.valueOf(endPercent) + ")");
        }

        int percentToDistribute = endPercent - startPercent;
        int numSpecies = chromosomes.size();
        for (int i = 1; i < numSpecies; i++) {
            chromosomes.get(i).mutate((int)(startPercent + (float) percentToDistribute * (float) i / (float) numSpecies));
        }
    }

    private static void crossSpecies(final List<SudokuChromosome> chromosomes) {
        int numSpecies = chromosomes.size();
        for (int i = 1; i < numSpecies; i ++) {
            chromosomes.get(i).uniformCrossover(chromosomes.get(0));
        }

    }

    private static void rankAndSortSpecies(final SudokuPuzzle puzzle, final List<SudokuChromosome> chromosomes) {
        for (final SudokuChromosome chromosome : chromosomes) {
            chromosome.setRank(puzzle.verify(chromosome.getGenes()));
        }

        Collections.sort(chromosomes, (o1, o2) -> {
            if (o1 == null || o2 == null) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o1 == null) {
                    return 1;
                } else {
                    return -1;
                }
            }
            if (Objects.equals(o1.getRank(), o2.getRank())) {
                return 0;
            }
            return (o1.getRank() > o2.getRank()) ? -1 : 1;
        });
    }
}
