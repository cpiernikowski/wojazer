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

    public static int[] ga(int n_pop, int n_iters, CitiesMap cm, double pm, double pc) {
        int[][] pop = create_population(n_pop, cm.n_of_vertices());
        int[] best = null;
        double min = Double.MAX_VALUE;

        for (int i = 0; i < n_iters; ++i) {
            // znajdz minimum - najlepszego osobnika dotychczas
            for (int j = 0; j < pop.length; ++j) {
                double y = chromosome_fitness(pop[j], cm);
                System.out.println("==Test: iteracja nr: " + i + " chromosom: " + Arrays.toString(pop[j]) + " y: " + y + " obecne min: " + min);
                if (min > y) {
                    min = y;
                    best = Arrays.copyOf(pop[j], pop[j].length);
                }
            }

            for (int j = 0; j < pop.length; ++j) {
                Random rnd = new Random();
                double current_pm = rnd.nextDouble(0, 1);
                double current_pc = rnd.nextDouble(0, 1);

                if (current_pm <= pm) {
                    pop[j] = mutate(pop[j]);
                }

                if (current_pc <= pc) {
                    int second_idx_cross = rnd.nextInt(0, pop.length);
                    while (second_idx_cross == j) {
                        second_idx_cross = rnd.nextInt(0, pop.length);
                    }

                    int[][] pmx_result = PMX.PMX_fun(pop[j], pop[second_idx_cross]);
                    pop[j] = pmx_result[0];
                    pop[second_idx_cross] = pmx_result[1];
                }
            }
            // ruletka tu
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
            System.out.println(cutPoint1 + " " + cutPoint2);
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
    }
}
