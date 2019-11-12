package logic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class CommonPool {
    private static final int HEAP_SIZE = 2000;
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final PriorityBlockingQueue<Item> mergeHeap = new PriorityBlockingQueue<>(HEAP_SIZE);

    public static ExecutorService getExecutor() {
        return executor;
    }

    public static PriorityBlockingQueue<Item> getMergeHeap() {
        return mergeHeap;
    }

}