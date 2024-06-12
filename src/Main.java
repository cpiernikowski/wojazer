import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    public static void save_result(int[] min, CitiesMap cm) throws IOException {
        /*
            Znalezione rozwiązanie należy zapisać do pliku,
            w którym powinna być podana liczba miast do odwiedzenia,
            sekwencja numerów lub nazw miast składających się na cykl Hamiltona,
            długość tego cyklu, odległości pomiędzy wyznaczonymi miastami
            oraz narastająco odległość przebyta przez komiwojażera pomiędzy tymi miastami.
            Np.: 1; 2; 5; 4; 3; 1; 1528;
            odległości:; 341; 299; 304; 280; 304;
            odl. narast.:; 0; 341; 640; 944; 1224; 1528;
        */
        File f = new File("out.txt");
        if (!f.exists()) {
            f.createNewFile();
        }

        PrintWriter pw = new PrintWriter(f);

        pw.println("Ilość miast do przebycia: " + (min.length+ 1));

        pw.print("Kolejność: ");
        for (int i = 0; i < min.length; ++i) {
            pw.print(min[i] + " ");
        }
        pw.println(min[0]);

        pw.println("Długość drogi: " + GeneticAlgorithm.chromosome_fitness(min, cm));

        pw.print("Odległości: ");
        for (int i = 0; i < min.length - 1; ++i) {
            pw.print(cm.distance_between(min[i], min[i + 1]) + " ");
        }
        pw.println(cm.distance_between(min[0], min[min.length - 1]));

        pw.print("Odległości narastająco: ");
        double o = 0.0;
        pw.print(o + " ");
        for (int i = 0; i < min.length - 1; ++i) {
            o += cm.distance_between(min[i], min[i + 1]);
            pw.print(o + " ");
        }
        pw.println(o + cm.distance_between(min[0], min[min.length - 1]));

        pw.close();
    }

//    public static void test_zaleznosci(CitiesMap cm) {
//        final int n_iteracji = 1000;
//
//        System.out.println("Test z pm=" + GeneticAlgorithm.pm + " i " + "pc=" + GeneticAlgorithm.pc);
//        GeneticAlgorithm.ga(10, n_iteracji, cm, GeneticAlgorithm.pm, GeneticAlgorithm.pc);
//        GeneticAlgorithm.ga(15, n_iteracji, cm, GeneticAlgorithm.pm, GeneticAlgorithm.pc);
//        GeneticAlgorithm.ga(20, n_iteracji, cm, GeneticAlgorithm.pm, GeneticAlgorithm.pc);
//        GeneticAlgorithm.ga(30, n_iteracji, cm, GeneticAlgorithm.pm, GeneticAlgorithm.pc);
//
//        System.out.println("Test z pm=" + 0.5 + " i " + "pc=" + 0.5);
//        GeneticAlgorithm.ga(10, n_iteracji, cm, 0.5, 0.5);
//        GeneticAlgorithm.ga(15, n_iteracji, cm, 0.5, 0.5);
//        GeneticAlgorithm.ga(20, n_iteracji, cm, 0.5, 0.5);
//        GeneticAlgorithm.ga(30, n_iteracji, cm, 0.5, 0.5);
//        /*
//            Jako że liczba permutacji rozwiązań dla naszego pliku wejsciowego nie jest zbyt wysoka,
//            to algorytm szybko znajduje najlepszą ścieżkę, często już w pierwszej iteracji,
//            po wielu wywołaniach testu można zauważyć, że ilość iteracji potrzebnych do znalezienia najlepszej drogi
//            dla tego pliku wejściowego rzadko przekracza 5; nie widać zależności pomiędzy szybkością znalezienia najlepszego rozwiązania,
//            a prawdopodobieństwem mutacji i krosowania, natomiast czym większa ilość populacji, tym większe prawdopodobieństwo na to, że
//            szybciej znajdziemy optymalną ścieżkę.
//        */
//    }

    public static void main(String[] args) throws IOException {
        File citiesFile = new File("in.txt");
        CitiesMap cm = new CitiesMap(citiesFile);
        System.out.println(cm.toString());

        int[] min = GeneticAlgorithm.ga(1000, 100, cm, GeneticAlgorithm.pm, GeneticAlgorithm.pc, true);
        save_result(min, cm);
       // test_zaleznosci(cm);
    }
}