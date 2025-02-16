package org.nash.eymultithreading;

public class Worker implements Runnable {

    private final MyQueue<Integer> queue;

    public Worker(MyQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            try {
                Integer request = queue.remove();
                System.out.println("Received request: " + request);

                Thread.sleep(request);

                System.out.println("Log of request: " + Math.log(request));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
