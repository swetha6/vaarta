package logic;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

class Sort<E extends Comparable<E>> implements Callable<List<E>> {
    private final List<E> list;

    public Sort(List<E> list) {
        this.list = list;
    }

    static <T extends Comparable<T>> void sortList(List<T> l) {
        if (notSorted(l)) {
            Collections.sort(l);
        }
    }

    private static <T extends Comparable<T>> boolean notSorted(List<T> l) {
        if (l.size() < 2) {
            return false;
        } else {
            int c = l.get(0).compareTo(l.get(1));
            for (int i = 2; i < l.size(); i++) {
                if (l.get(i - 1).compareTo(l.get(i)) != c) {
                    return true;
                }
            }
            return false;
        }
    }

    public List<E> call() {
        if (notSorted(list)) {
            Collections.sort(list);
        }
        return list;
    }
}
