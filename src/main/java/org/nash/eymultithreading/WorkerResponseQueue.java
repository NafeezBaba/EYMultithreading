package org.nash.eymultithreading;

public class WorkerResponseQueue {
    private final MyQueue<Double> responses;

    public WorkerResponseQueue(int capacity) {
        this.responses = new MyQueue<>(capacity);
    }

    public synchronized void addResponse(double response) {
        responses.add(response);
    }

    public synchronized Double pollResponse() {
        return responses.remove();
    }

    public synchronized boolean isEmpty() {
        return responses.isEmpty();
    }
}
