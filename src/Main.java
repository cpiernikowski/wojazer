import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        CitiesMap cm = new CitiesMap(new File("in.txt"));

        int[] min = GeneticAlgorithm.ga(10, 5, cm, GeneticAlgorithm.pm, GeneticAlgorithm.pc);
        System.out.println(Arrays.toString(min));
    }
}