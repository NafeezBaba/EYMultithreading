package org.nash.eymultithreading;

public class WorkerWithPriority implements Runnable {

    private final MyPriorityQueue<Integer> queue;
    private static long lastPriority100CompletionTime = -1;
    private static long lastPriority0CompletionTime = -1;

    public WorkerWithPriority(MyPriorityQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            try {
                Integer request = queue.remove();
                if (request == null) break;

                long startTime = System.currentTimeMillis();
                System.out.println("Worker received request: " + request);
                Thread.sleep(request);
                System.out.println("Log of " + request + ": " + Math.log(request));

                if (request % 200 == 0) {
                    lastPriority0CompletionTime = System.currentTimeMillis() - startTime;
                } else {
                    lastPriority100CompletionTime = System.currentTimeMillis() - startTime;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static long getLastPriority100CompletionTime() {
        return lastPriority100CompletionTime;
    }

    public static long getLastPriority0CompletionTime() {
        return lastPriority0CompletionTime;
    }
}
