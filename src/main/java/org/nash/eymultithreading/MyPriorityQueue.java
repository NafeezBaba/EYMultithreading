package org.nash.eymultithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyPriorityQueue<T> {
    private final int capacity;
    private final List<Entry<T>> items;
    private final Lock lock;
    private final Condition isFullCondition;
    private final Condition isEmptyCondition;
    private final AtomicBoolean isEndOfQueue;

    public MyPriorityQueue(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>(capacity);
        this.lock = new ReentrantLock();
        this.isFullCondition = lock.newCondition();
        this.isEmptyCondition = lock.newCondition();
        this.isEndOfQueue = new AtomicBoolean(false);
    }

    public void add(T item, int priority) {
        lock.lock();
        try {
            while (items.size() == capacity) {
                try {
                    isFullCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Insert item in sorted order (lower priority value = higher priority)
            Entry<T> newEntry = new Entry<>(item, priority);
            int index = 0;
            while (index < items.size() && items.get(index).priority <= priority) {
                index++;
            }
            items.add(index, newEntry);
            isEmptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T remove() {
        lock.lock();
        try {
            while (items.isEmpty()) {
                if (isEndOfQueue.get()) return null;
                try {
                    isEmptyCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            System.out.println(Thread.currentThread().getName() + ": Removing " + items.size() + " items");
            T item = items.remove(0).item;
            isFullCondition.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

    public void endOfQueue() {
        lock.lock();
        try {
            isEndOfQueue.set(true);
            isEmptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return items.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    private static class Entry<T> {
        private final T item;
        private final int priority;

        public Entry(T item, int priority) {
            this.item = item;
            this.priority = priority;
        }
    }
}