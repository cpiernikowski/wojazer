import java.util.Objects;

public class Pair <T> {
    T x;
    T y;

    private int hash = Objects.hash(x, y);

    Pair(T a, T b) {
        x = a;
        y = b;
    }

    @Override
    public String toString() {
        return x.toString() + " " + y.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o .getClass())
            return false;

        Pair<Integer> other = (Pair<Integer>)o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }
}
