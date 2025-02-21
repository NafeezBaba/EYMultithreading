package org.nash.eymultithreading;

public class DAGWorker implements Runnable {
    private MyBlockingQueue<Node> workerQueue;
    private MyBlockingQueue<Node> workerResponseQueue;

    public DAGWorker(MyBlockingQueue<Node> workerQueue, MyBlockingQueue<Node> workerResponseQueue) {
        this.workerQueue = workerQueue;
        this.workerResponseQueue = workerResponseQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Node node = workerQueue.remove();
                if (node == null)
                    break;

                node.result = node.execute();
                workerResponseQueue.add(node);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
