package trufanov.ao1.data;

import java.util.List;

public interface ResultHolder<T> {

    List<T> get();

    void add(T t);

    default void addAll(Iterable<T> iterable) {
        for (T t : iterable) {
            add(t);
        }
    }
}
