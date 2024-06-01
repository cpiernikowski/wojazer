import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Graph {
    private HashMap<Pair<Integer>, Double> edges;
    private int n_vertices;
    Graph(File f) throws IOException {
        this.edges = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(f));
        this.n_vertices = 0;
        while (reader.readLine() != null)
            ++this.n_vertices;

        reader = new BufferedReader(new FileReader(f));

        int row = 0;
        while (reader.ready()) {
            String line = reader.readLine();
            String[] vals = line.split(" ");
            assert vals.length == this.n_vertices; // zaklada, ze dane sa w poprawnym formacie, po stronie uzytkownika programu stoi zweryfikowanie ich poprawnosci
            for (int col = 0; col < this.n_vertices; ++col) {
                edges.put(new Pair<>(row + 1, col + 1), Double.valueOf(vals[col]));
                edges.put(new Pair<>(col + 1, row + 1), Double.valueOf(vals[col]));
            }
            ++row;
        }

        reader.close();
    }

    public HashMap<Pair<Integer>, Double> get_edges() {
        return this.edges;
    }

    public double distance_between(int a, int b) {
        assert a <= this.n_vertices && b <= this.n_vertices;
        return edges.get(new Pair<Integer>(a, b));
    }

    public double distance_between(Pair<Integer> p) {
        return distance_between(p.x, p.y);
    }
}
