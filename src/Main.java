import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        CitiesMap cm = new CitiesMap(new File("in.txt"));

        int[] ch = GeneticAlgorithm.random_tsp_solution(cm.n_of_vertices());

        System.out.println(Arrays.toString(ch));
        double d_sztywno = cm.distance_between(ch[0], ch[1])
                           + cm.distance_between(ch[1], ch[2])
                           + cm.distance_between(ch[2], ch[3])
                           + cm.distance_between(ch[3], ch[4])
                           + cm.distance_between(ch[4], ch[0]);

        double d_fn = GeneticAlgorithm.chromosome_fitness(ch, cm);

        assert d_sztywno == d_fn;

        System.out.println(d_sztywno);
        System.out.println(d_fn);
    }
}