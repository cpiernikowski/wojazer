//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        Graph g = new Graph(new File("in.txt"));
        for (Map.Entry<Pair<Integer>, Double> s : g.get_edges().entrySet()) {
            System.out.println(s);
        }
        System.out.println(g.distance_between(2, 3));
        System.out.println(g.distance_between(3,2));
    }
}