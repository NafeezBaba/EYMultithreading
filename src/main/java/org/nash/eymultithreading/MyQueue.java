package org.nash.eymultithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyQueue<T> {

    private final int capacity;
    private final List<T> items;
    private final Condition isFullCondition;
    private final Condition isEmptyCondition;
    private final Lock lock;
    private final AtomicBoolean isEndOfQueue;

    public MyQueue(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>(capacity);
        this.lock = new ReentrantLock();
        this.isFullCondition = lock.newCondition();
        this.isEmptyCondition = lock.newCondition();
        this.isEndOfQueue = new AtomicBoolean(false);
    }

    public void add(T t) {
        lock.lock();
        try {
            while (items.size() == capacity) {
                try {
                    isFullCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            items.add(t);
            isEmptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T remove() {
        T t;
        lock.lock();
        try {
            while (items.isEmpty()) {
                try {
                    isEmptyCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            t = items.removeFirst();
            isFullCondition.signal();
        } finally {
            lock.unlock();
        }

        return t;
    }

    public void endOfQueue() {
        isEndOfQueue.set(true);
    }

    public final boolean isEmpty() {
        boolean isEmpty;
        while (!isEndOfQueue.get()) {
            // Just wait
        }
        isEmpty = items.isEmpty();

        return isEmpty;
    }
}
