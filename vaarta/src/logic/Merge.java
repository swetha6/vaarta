package logic;

import java.util.List;
import java.util.ListIterator;

public class Merge {
    public static <E extends Comparable<E>> void simpleMerge(List<E> l1, List<E> l2, List<E> l0) {
        if (l1 == null || l2 == null || l0 == null) {
            throw new IllegalArgumentException();
        }

        ListIterator<E> i1 = l1.listIterator();
        ListIterator<E> i2 = l2.listIterator();

        while (i1.hasNext() && i2.hasNext()) {
            E e1 = l1.get(i1.nextIndex());
            E e2 = l2.get(i2.nextIndex());

            if (e1 == null) {
                i1.next();
            } else if (e2 == null) {
                i2.next();
            } else if (e1.compareTo(e2) > 0) {
                l0.add(i2.next());
            } else {
                l0.add(i1.next());
            }
        }

        while (i1.hasNext()) {
            l0.add(i1.next());
        }

        while (i2.hasNext()) {
            l0.add(i2.next());
        }
    }
}
