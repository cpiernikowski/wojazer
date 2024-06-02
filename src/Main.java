import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        Graph g = new Graph(new File("in.txt"));

        int[] ch = GeneticAlgorithm.random_tsp_solution(g.n_of_vertices());

        System.out.println(Arrays.toString(ch));
        double d_sztywno = g.distance_between(ch[0], ch[1])
                           + g.distance_between(ch[1], ch[2])
                           + g.distance_between(ch[2], ch[3])
                           + g.distance_between(ch[3], ch[4])
                           + g.distance_between(ch[4], ch[0]);

        double d_fn = GeneticAlgorithm.chromosome_fitness(ch, g);

        assert d_sztywno == d_fn;

        System.out.println(d_sztywno);
        System.out.println(d_fn);
    }
}