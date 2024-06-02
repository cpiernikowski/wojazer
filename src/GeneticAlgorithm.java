import java.util.Random;

public class GeneticAlgorithm {

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

    public static double chromosome_fitness(int[] ch, Graph g) {
        assert ch.length <= g.n_of_vertices();
        double out = 0.0;

        for (int i = 0; i < ch.length - 1; ++i) {
            out += g.distance_between(ch[i], ch[i + 1]);
        }

        out += g.distance_between(ch[ch.length - 1], ch[0]);

        return out;
    }
}
