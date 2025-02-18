package org.nash.eymultithreading;

public class WorkerWithPriority implements Runnable {

    private final MyPriorityQueue<Integer> queue;

    public WorkerWithPriority(MyPriorityQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            try {
                Integer request = queue.remove();
                if (request == null) break;

                System.out.println("Worker received request: " + request);

                Thread.sleep(10);

                System.out.println("Log of " + request + ": " + Math.log(request));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
