import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CitiesMap {
    private double[][] mat;
    private int n_vertices;

    CitiesMap(File f) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        this.n_vertices = 0;
        while (reader.readLine() != null)
            ++this.n_vertices;

        reader = new BufferedReader(new FileReader(f));

        mat = new double[this.n_vertices][this.n_vertices];

        int row = 0;
        while (reader.ready()) {
            String line = reader.readLine();
            String[] vals = line.split(" ");
            assert vals.length == this.n_vertices; // zaklada, ze dane sa w poprawnym formacie, po stronie uzytkownika programu stoi zweryfikowanie ich poprawnosci
            for (int col = 0; col < this.n_vertices; ++col) {
                mat[col][row] = Double.valueOf(vals[col]);
            }
            ++row;
        }

        reader.close();
    }

    public double[][] get_mat() {
        return this.mat;
    }

    public double distance_between(int a, int b) {
        assert a <= this.n_vertices && b <= this.n_vertices;
        return this.mat[a - 1][b - 1];
    }

    public int n_of_vertices() {
        return n_vertices;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cities Map:\n");
        for (int i = 0; i < n_vertices; i++) {
            for (int j = 0; j < n_vertices; j++) {
                sb.append(mat[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
