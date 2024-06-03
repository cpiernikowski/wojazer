import java.util.*;

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

    public static void main(String[] args) {
        // test pmxa
        int[] parent1 = {1, 2, 3, 5, 4};
        int[] parent2 = {3, 4, 5, 2, 1};

        int[][] offspring = PMX.PMX_fun(parent1, parent2);

        System.out.println("Offspring 1: " + Arrays.toString(offspring[0]));
        System.out.println("Offspring 2: " + Arrays.toString(offspring[1]));
    }

        public static class PMX {
            static int[] offspring1; // Inicjalizacja tablicy dla pierwszego dziecka
            static int[] offspring2; // Inicjalizacja tablicy dla drugiego dziecka
            static int[] segment1; // Inicjalizacja tablicy dla segmentu 1
            static int[] segment2; // Inicjalizacja tablicy dla segmentu 2
            static int cutPoint1; // Inicjalizacja pierwszego punktu cięcia
            static int cutPoint2; // Inicjalizacja drugiego punktu cięcia

            public static int[][] PMX_fun(int[] parent1, int[] parent2) {
                Random firstRNum = new Random(); // Generator losowych liczb dla pierwszego punktu cięcia
                Random secondRNum = new Random(); // Generator losowych liczb dla drugiego punktu cięcia

                // Ustalenie górnej granicy dla losowych punktów cięcia
                int randomNo_Boundary = (parent1.length) - 1;
                offspring1 = new int[parent1.length]; // Inicjalizacja tablicy dla pierwszego dziecka
                offspring2 = new int[parent2.length]; // Inicjalizacja tablicy dla drugiego dziecka

                // Losowanie punktów cięcia
                cutPoint1 = firstRNum.nextInt(randomNo_Boundary);
                cutPoint2 = secondRNum.nextInt(randomNo_Boundary);

                // Upewnienie się, że punkty cięcia są różne
                while (cutPoint1 == cutPoint2) {
                    cutPoint2 = secondRNum.nextInt(randomNo_Boundary);
                }

                // Zapewnienie poprawnej kolejności punktów cięcia
                if (cutPoint1 > cutPoint2) {
                    int temp = cutPoint1;
                    cutPoint1 = cutPoint2;
                    cutPoint2 = temp;
                }

                // Wydrukowanie punktów cięcia dla debugowania
                System.out.println(cutPoint1 + " " + cutPoint2);

                // Utworzenie środkowych segmentów na podstawie punktów cięcia
                create_Segments(parent1, parent2, cutPoint1, cutPoint2);

                // Wykonanie krzyżowania
                crossOver(offspring1, parent1);
                crossOver(offspring2, parent2);

                // Zwrócenie wynikowych dzieci
                return new int[][]{offspring1, offspring2};
            }

            // Funkcja sprawdza czy występują duplikaty
            private static boolean check_forDuplicates(int[] offspring, int indexOfElement) {
                // Iteracja przez potomka
                for (int index = 0; index < offspring.length; index++) {
                    // Sprawdzenie, czy występuje duplikat
                    if ((offspring[index] == offspring[indexOfElement]) && (indexOfElement != index)) {
                        return true; // Zwrócenie true, jeśli duplikat został znaleziony
                    }
                }
                return false; // Zwrócenie false, jeśli nie znaleziono duplikatu
            }

            // Jeśli element jest zduplikowany, zastąp go, używając jego mapowania
            private static void sort_Duplicates(int[] offspring, int indexOfElement) {
                // Iteracja przez segmenty
                for (int index = 0; index < segment1.length; index++) {
                    // Sprawdzenie, czy wartość z segmentu 1 pasuje do wartości potomka
                    if (segment1[index] == offspring[indexOfElement]) {
                        offspring[indexOfElement] = segment2[index]; // Zamiana wartości na odpowiadającą wartość z segmentu 2
                    } else if (segment2[index] == offspring[indexOfElement]) {
                        offspring[indexOfElement] = segment1[index]; // Zamiana wartości na odpowiadającą wartość z segmentu 1
                    }
                }
            }

            // Utworzenie segmentów na podstawie punktów cięcia
            private static void create_Segments(int[] parent1, int[] parent2, int cutPoint1, int cutPoint2) {
                // Obliczenie wielkości segmentów
                int capacity_ofSegments = (cutPoint2 - cutPoint1) + 1;
                segment1 = new int[capacity_ofSegments]; // Inicjalizacja segmentu 1
                segment2 = new int[capacity_ofSegments]; // Inicjalizacja segmentu 2

                int segment1and2Index = 0; // Inicjalizacja indeksu dla segmentów
                for (int index = 0; index < parent1.length; index++) {
                    // Wybór elementów między punktami cięcia
                    if ((index >= cutPoint1) && (index <= cutPoint2)) {
                        int x = parent1[index];
                        int y = parent2[index];
                        segment1[segment1and2Index] = x; // Przypisanie wartości do segmentu 1
                        segment2[segment1and2Index] = y; // Przypisanie wartości do segmentu 2
                        segment1and2Index++; // Aktualizacja indeksu segmentów
                    }
                }
            }

            // Wstawienie segmentów do potomka
            private static void insert_Segments(int[] offspring, int[] segment) {
                int segmentIndex = 0; // Inicjalizacja indeksu segmentu
                for (int index = 0; index < offspring.length; index++) {
                    // Sprawdzenie, czy indeks jest w obrębie punktów cięcia
                    if ((index >= cutPoint1) && (index <= cutPoint2)) {
                        offspring[index] = segment[segmentIndex]; // Wstawienie segmentu do potomka
                        segmentIndex++; // Aktualizacja indeksu segmentu
                    }
                }
            }

            // Krzyżowanie potomka z rodzicami
            public static void crossOver(int[] offspring, int[] parentX) {
                // Wybór odpowiedniego segmentu do wstawienia do potomka
                if (offspring == offspring1) {
                    int[] segment = segment2;
                    insert_Segments(offspring, segment);
                } else if (offspring == offspring2) {
                    int[] segment = segment1;
                    insert_Segments(offspring, segment);
                }

                // Iteracja przez potomków
                for (int index = 0; index < offspring.length; index++) {
                    // Wypełnienie wartościami spoza punktów cięcia
                    if ((index < cutPoint1) || (index > cutPoint2)) {
                        offspring[index] = parentX[index];
                    }
                }

                // Iteracja przez potomków
                for (int index = 0; index < offspring.length; index++) {
                    // Usuwanie duplikatów
                    if ((index < cutPoint1) || (index > cutPoint2)) {
                        while (check_forDuplicates(offspring, index)) {
                            sort_Duplicates(offspring, index);
                        }
                    }
                }
            }
        }
    }

