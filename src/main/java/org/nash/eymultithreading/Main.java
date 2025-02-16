package org.nash.eymultithreading;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        MyQueue<Integer> queue = new MyQueue<>(100);

        int nThreads = args.length > 0 ? Integer.parseInt(args[0]) : 10;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        for(int i = 0; i < nThreads; i++) {
            executor.execute(new Worker(queue));
        }

        // Publish events to queue
        long start = Instant.now().toEpochMilli();
        for (int i = 100; i <= 10000; i += 100) {
            queue.add(i);
        }
        queue.endOfQueue();
        long endPublish = Instant.now().toEpochMilli();
        System.out.println("Main Thread end: Time taken to publish: " + (endPublish - start) + " ms");

        // Wait for executor to finish
        try {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long end = Instant.now().toEpochMilli();
        System.out.println("All tasks processed in " + (end - start) + " ms.");
    }
}
