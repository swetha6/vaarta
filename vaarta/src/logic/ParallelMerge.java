package logic;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ParallelMerge<E extends Comparable<E>> implements Runnable {
    private final List<E> l0;
    private final int lo0;
    private final int hi0;
    private List<E> l1;
    private int lo1;
    private int hi1;
    private List<E> l2;
    private int lo2;
    private int hi2;


    public ParallelMerge(List<E> l1, int lo1, int hi1, List<E> l2, int lo2, int hi2, List<E> l0, int lo0, int hi0) {
        this.l1 = l1;
        this.lo1 = lo1;
        this.hi1 = hi1;
        this.l2 = l2;
        this.lo2 = lo2;
        this.hi2 = hi2;
        this.l0 = l0;
        this.lo0 = lo0;
        this.hi0 = hi0;
    }

    public void run() {
        int s1 = hi1 - lo1;
        int s2 = hi2 - lo2;

        if (s1 < s2) {
            // Swap lists
            List<E> t = l1;
            l1 = l2;
            l2 = t;

            // Swap indices
            int u = lo1;
            lo1 = lo2;
            lo2 = u;
            u = hi1;
            hi1 = hi2;
            hi2 = u;
        }

        if (s1 <= 0) {
            // Lists are empty
            return;
        }

        int mid1 = (lo1 + hi1) / 2;
        E e1 = l1.get(mid1);
        int mid2 = Collections.binarySearch(l2, e1);
        int mid0 = lo0 + (mid1 - lo1) + (mid2 - lo2);
        l0.set(mid0, e1);

        ExecutorService exec = CommonPool.getExecutor();
        ParallelMerge<E> merge1 = new ParallelMerge<>(l1, lo1, mid1, l2, lo2, mid2, l0, lo0, mid2);
        ParallelMerge<E> merge2 = new ParallelMerge<>(l1, mid1 + 1, hi1, l2, mid2, hi2, l0, mid2 + 1, hi0);
        exec.submit(merge1);
        exec.submit(merge2);
    }
}
