package org.nash.eymultithreading;

public class WorkerWithBlockingQueue implements Runnable {
    private final MyBlockingQueue<Integer> queue;

    public WorkerWithBlockingQueue(MyBlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Integer request = queue.remove();
                if (request == null)
                    break;

                System.out.println("Worker received request: " + request);
                Thread.sleep(request);
                System.out.println("Log of request " + request + ": " + Math.log(request));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
