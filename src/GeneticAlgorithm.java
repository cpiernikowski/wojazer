import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GeneticAlgorithm {

    // default
    static final double pm = 0.2;
    static final double pc = 0.2;

    public static class Util {
        public static boolean contains(int[] haystack, int needle) {
            for (int e : haystack) {
                if (e == needle) {
                    return true;
                }
            }
            return false;
        }
    }

    public static int[] random_tsp_solution(int max) {
        // specjalnie bez koncowego miasta (ktore jest takie same jak poczatkowe), jest to wymagane, poniewaz w finalnym algorytmie bedzie
        // uzywane krosowanie pmx, ktore wymaga, aby kazdy gen byl unikalny
        int[] out = new int[max];
        Random rnd = new Random();

        for (int i = 0; i < max; ++i) {
            int r = rnd.nextInt(1, max + 1);

            while (Util.contains(out, r)) {
                r = rnd.nextInt(1, max + 1);
            }

            out[i] = r;
        }

        return out;
    }

    public static int[] mutate(int[] ch) {
        // zamienia miejscami dwa geny w chromosomie
        Random rnd = new Random();
        int idxa = rnd.nextInt(ch.length);
        int idxb = rnd.nextInt(ch.length);

        while (idxa == idxb) {
            idxb = rnd.nextInt(ch.length);
        }

        int tmp = ch[idxa];
        ch[idxa] = ch[idxb];
        ch[idxb] = tmp;

        return ch;
    }

    public static double chromosome_fitness(int[] ch, CitiesMap cm) {
        assert ch.length <= cm.n_of_vertices();
        double out = 0.0;

        for (int i = 0; i < ch.length - 1; ++i) {
            out += cm.distance_between(ch[i], ch[i + 1]);
        }

        out += cm.distance_between(ch[ch.length - 1], ch[0]);

        return out;
    }

    public static int[][] create_population(int n, int max) {
        int[][] out = new int[n][max];

        for (int i = 0; i < n; ++i) {
            out[i] = random_tsp_solution(max);
        }

        return out;
    }

    public static double bestFitness(int[][] pop, CitiesMap cm, int[] bestIndividual, double[] minFitness) {
        for (int j = 0; j < pop.length; ++j) {
            double fitness = chromosome_fitness(pop[j], cm); // Calculate fitness of the j-th individual

            // Check if the current individual is the best one found so far
            if (fitness < minFitness[0]) {
                minFitness[0] = fitness; // Update the minimum fitness value
                System.arraycopy(pop[j], 0, bestIndividual, 0, pop[j].length); // Copy the best individual to bestIndividual array
            }
        }

        return minFitness[0]; // Return the minimum fitness value
    }


    public static int[] ga(int n_pop, int n_iters, CitiesMap cm, double pm, double pc, boolean Roulette) {
        int[][] pop = create_population(n_pop, cm.n_of_vertices());
        int[] best = new int[cm.n_of_vertices()];
        double[] min = new double[]{Double.MAX_VALUE};
        int temp = n_iters/20;

        int[][] populationClone;
        double[][] allBestFitness = new double[50][n_iters / temp + 1];
        for (int run = 0; run < 50; run++) {
            best = new int[cm.n_of_vertices()];
            min = new double[]{Double.MAX_VALUE};
            populationClone = pop;
            double runBestFitness = Double.MAX_VALUE; // Initialize run-specific best fitness

            for (int i = 0; i < n_iters; ++i) {
                double bestFitness = bestFitness(populationClone, cm, best, min);

                if ((i % temp) == 0) {
                    runBestFitness = Math.min(runBestFitness, bestFitness); // Update run-specific best fitness
                    allBestFitness[run][i / temp] = runBestFitness;
                    System.out.println("Run " + (run + 1) + ", Iteracja " + i + " - Best fitness: " + bestFitness);
                }

                if(Roulette)
                    populationClone = PMX.selectionByRoulette(populationClone, cm);
                else
                    populationClone = PMX.selectionByTournament(populationClone, cm,3);

                for (int j = 0; j < populationClone.length; ++j) {
                    Random rnd = new Random();
                    double current_pm = rnd.nextDouble(0, 1);
                    double current_pc = rnd.nextDouble(0, 1);

                    if (current_pm <= pm) {
                        populationClone[j] = mutate(populationClone[j]);
                    }

                    if (current_pc <= pc) {
                        int second_idx_cross = rnd.nextInt(0, populationClone.length);
                        while (second_idx_cross == j) {
                            second_idx_cross = rnd.nextInt(0, populationClone.length);
                        }

                        int[][] pmx_result = PMX.PMX_fun(populationClone[j], populationClone[second_idx_cross]);
                        populationClone[j] = pmx_result[0];
                        populationClone[second_idx_cross] = pmx_result[1];
                    }
                }
            }
        }

        try (FileWriter writer = new FileWriter("best_fitness.txt")) {
            for (int run = 0; run < 50; run++) {
                for (int i = 0; i <= n_iters / temp; i++) {
                    writer.write("Run: " + (run + 1) + ", Iteracja: " + (i * temp) + ", Best fitness: " + allBestFitness[run][i] + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return best;
    }

    public static void main(String[] args) {
        // test pmxa
        int[] parent1 = {1, 2, 3, 5, 4};
        int[] parent2 = {3, 4, 5, 2, 1};

        int[][] offspring = PMX.PMX_fun(parent1, parent2);

        System.out.println("Offspring 1: " + Arrays.toString(offspring[0]));
        System.out.println("Offspring 2: " + Arrays.toString(offspring[1]));
    }


    public static class PMX {
        static int[] offspring1;
        static int[] offspring2;
        static int[] segment1;
        static int[] segment2;
        static int cutPoint1;
        static int cutPoint2;
        public static int[][] PMX_fun(int[] parent1, int[] parent2) {
            Random firstRNum = new Random();
            Random secondRNum = new Random();

            int randomNo_Boundary = (parent1.length) - 1;
            offspring1 = new int[parent1.length];
            offspring2 = new int[parent2.length];
            cutPoint1 = firstRNum.nextInt(randomNo_Boundary);
            cutPoint2 = secondRNum.nextInt(randomNo_Boundary);


            while (cutPoint1 == cutPoint2) {
                cutPoint2 = secondRNum.nextInt(randomNo_Boundary);
            }
            if (cutPoint1 > cutPoint2) {
                int temp = cutPoint1;
                cutPoint1 = cutPoint2;
                cutPoint2 = temp;
            }
            //System.out.println(cutPoint1 + " " + cutPoint2);
            create_Segments(parent1, parent2, cutPoint1, cutPoint2);
            crossOver(offspring1, parent1, parent2);
            crossOver(offspring2, parent2, parent1);

            return new int[][]{offspring1, offspring2};


        }
        private static boolean check_forDuplicates(int[] offspring, int indexOfElement) {
            for (int index = 0; index < offspring.length; index++) {
                if ((offspring[index] == offspring[indexOfElement]) &&
                        (indexOfElement != index)) {
                    return true;
                }
            }
            return false;
        }

        private static void sort_Duplicates(int[] offspring, int indexOfElement) {
            for (int index = 0; index < segment1.length; index++) {
                if (segment1[index] == offspring[indexOfElement]) {
                    offspring[indexOfElement] = segment2[index];
                } else if (segment2[index] == offspring[indexOfElement]) {
                    offspring[indexOfElement] = segment1[index];
                }
            }
        }

        private static void create_Segments(int[] parent1, int[] parent2, int cutPoint1, int cutPoint2) {
            int capacity_ofSegments = (cutPoint2 - cutPoint1) + 1;
            segment1 = new int[capacity_ofSegments];
            segment2 = new int[capacity_ofSegments];
            int segment1and2Index = 0;
            for (int index = 0; index < parent1.length; index++) {
                if ((index >= cutPoint1) && (index <= cutPoint2)) {
                    int x = parent1[index];
                    int y = parent2[index];
                    segment1[segment1and2Index] = x;
                    segment2[segment1and2Index] = y;
                    segment1and2Index++;
                }
            }
        }

        private static void insert_Segments(int[] offspring, int[] segment) {
            int segmentIndex = 0;
            for (int index = 0; index < offspring.length; index++) {
                if ((index >= cutPoint1) && (index <= cutPoint2)) {
                    offspring[index] = segment[segmentIndex];
                    segmentIndex++;
                }
            }
        }

        public static void crossOver(int[] offspring, int[] parentX, int[] parentY) {
            if (offspring == offspring1) {
                int[] segment = segment2;
                insert_Segments(offspring, segment);
            } else if (offspring == offspring2) {
                int[] segment = segment1;
                insert_Segments(offspring, segment);
            }

            for (int index = 0; index < offspring.length; index++) {
                if ((index < cutPoint1) || (index > cutPoint2)) {
                    offspring[index] = parentX[index];
                }
            }

            for (int index = 0; index < offspring.length; index++) {
                if ((index < cutPoint1) || (index > cutPoint2)) {
                    while (check_forDuplicates(offspring, index)) {
                        sort_Duplicates(offspring, index);
                    }
                }
            }
        }
        public static int[][] selectionByRoulette(int[][] population, CitiesMap cm) {
            double[] fitnessValues = new double[population.length];

            // Oblicz fitness dla każdej jednostki w populacji
            for (int i = 0; i < population.length; i++) {
                fitnessValues[i] = 1.0 / chromosome_fitness(population[i], cm); // Używamy odwrotności długości trasy
            }

            double totalFitness = 0.0;
            double[] probabilities = new double[fitnessValues.length];

            // Calculate total fitness
            for (double fitness : fitnessValues) {
                totalFitness += fitness;
            }

            // Calculate selection probabilities
            for (int i = 0; i < fitnessValues.length; i++) {
                probabilities[i] = fitnessValues[i] / totalFitness;
            }

            // Cumulative probabilities
            double[] cumulativeProbabilities = new double[probabilities.length];
            cumulativeProbabilities[0] = probabilities[0];
            for (int i = 1; i < probabilities.length; i++) {
                cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + probabilities[i];
            }

            // Select individuals based on roulette wheel
            int[][] selectedPopulation = new int[population.length][];
            Random random = new Random();
            for (int i = 0; i < population.length; i++) {
                double rand = random.nextDouble();
                for (int j = 0; j < cumulativeProbabilities.length; j++) {
                    if (rand <= cumulativeProbabilities[j]) {
                        selectedPopulation[i] = Arrays.copyOf(population[j], population[j].length);
                        break;
                    }
                }
            }

            return selectedPopulation;
        }
        public static int[][] selectionByTournament(int[][] population, CitiesMap cm, int tournamentSize) {
            int[][] selectedPopulation = new int[population.length][];
            Random random = new Random();
            double[] fitnessValues = new double[population.length];

            // Calculate fitness values
            for (int i = 0; i < population.length; i++) {
                fitnessValues[i] = chromosome_fitness(population[i], cm);
            }

            for (int i = 0; i < population.length; i++) {
                // Select random individuals for the tournament
                int[] tournamentParticipants = new int[tournamentSize];
                for (int j = 0; j < tournamentSize; j++) {
                    tournamentParticipants[j] = random.nextInt(population.length);
                }

                // Find the winner of the tournament (individual with the lowest fitness value)
                int winnerIndex = tournamentParticipants[0];
                double minFitness = fitnessValues[tournamentParticipants[0]];
                for (int participantIndex : tournamentParticipants) {
                    if (fitnessValues[participantIndex] < minFitness) {
                        minFitness = fitnessValues[participantIndex];
                        winnerIndex = participantIndex;
                    }
                }

                // Add the winner to the selected population
                selectedPopulation[i] = Arrays.copyOf(population[winnerIndex], population[winnerIndex].length);
            }

            return selectedPopulation;
        }
    }
}
