package org.nash.eymultithreading;

public class WorkerWithResponse implements Runnable {
    private final MyBlockingQueue<Integer> myBlockingQueue;
    private final WorkerResponseQueue workerResponseQueue;

    public WorkerWithResponse(MyBlockingQueue<Integer> myBlockingQueue, WorkerResponseQueue workerResponseQueue) {
        this.myBlockingQueue = myBlockingQueue;
        this.workerResponseQueue = workerResponseQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Integer request = myBlockingQueue.remove();
                if (request == null)
                    break;

                System.out.println("Worker received request: " + request);
                Thread.sleep(request);
                double logResult = Math.log(request);
                System.out.println("Log of request " + request + ": " + logResult);
                workerResponseQueue.addResponse(logResult);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
