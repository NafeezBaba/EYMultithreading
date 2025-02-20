package org.nash.eymultithreading;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyBlockingQueue<T> {
    private final int capacity;
    private final LinkedList<T> queue;
    private final Lock lock;
    private final Condition notFullCondition;
    private final Condition notEmptyCondition;

    public MyBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notFullCondition = lock.newCondition();
        this.notEmptyCondition = lock.newCondition();
    }

    public void add(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFullCondition.await();
            }
            queue.add(item);
            notEmptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T remove() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmptyCondition.await();
            }
            T item = queue.removeFirst();
            notFullCondition.signalAll();
            return item;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return queue.size() == capacity;
        } finally {
            lock.unlock();
        }
    }
}

