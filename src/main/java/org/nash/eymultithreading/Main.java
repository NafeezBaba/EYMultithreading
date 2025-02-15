package org.nash.eymultithreading;

public class Main {

    public static void main(String[] args) {
        int capacity = args.length > 0 ? Integer.parseInt(args[0]) : 100;
        MyQueue<Integer> queue = new MyQueue<>(capacity);
        int counter = 0;
        for (int i = 1; i <= 1000; i++) {
            if (++counter == 100) {
                queue.add(i);
                counter = 0;
            }
        }
    }
}
