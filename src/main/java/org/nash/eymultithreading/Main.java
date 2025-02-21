package org.nash.eymultithreading;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        //assignmentPart1_2(args);

        //assignmentPart3();

        //assignmentPart4();

        //assignmentPart5();

        //DAGAssignment2();
    }

    private static void DAGAssignment2() throws InterruptedException {
        MyBlockingQueue<String> requestQueue = new MyBlockingQueue<>(10);
        MyBlockingQueue<Double> responseQueue = new MyBlockingQueue<>(10);
        MyBlockingQueue<Node> workerQueue = new MyBlockingQueue<>(10);
        MyBlockingQueue<Node> workerResponseQueue = new MyBlockingQueue<>(10);

        // Create and start the manager
        DAGManager manager = new DAGManager(requestQueue, responseQueue, workerQueue, workerResponseQueue);
        Thread managerThread = new Thread(manager);
        managerThread.start();

        int numWorkers = 2;
        for (int i = 0; i < numWorkers; i++) {
            DAGWorker worker = new DAGWorker(workerQueue, workerResponseQueue);
            Thread workerThread = new Thread(worker);
            workerThread.start();
        }

        String equation = "2+2";
        requestQueue.add(equation);

        double result = responseQueue.remove();
        DAGNode dag = manager.getDag();
        System.out.println("Number of Nodes = " + dag.getNumberOfNodes());
        System.out.println("Answer = " + result);
        System.out.println("Batches Sent For Execution = " + manager.getBatchCount());

        // Shutdown
        requestQueue.add(null);
        managerThread.join();
    }

    private static void assignmentPart5() throws InterruptedException {
        int numberOfWorkers = 3;
        MyBlockingQueue<Integer> blockingQueue = new MyBlockingQueue<>(5);
        WorkerResponseQueue workerResponseQueue = new WorkerResponseQueue(50);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfWorkers);

        long startTime = System.currentTimeMillis();
        for (int i = 100; i <= 1000; i += 100) {
            blockingQueue.add(i);
        }
        for (int i = 0; i < numberOfWorkers; i++) {
            executorService.submit(new WorkerWithResponse(blockingQueue, workerResponseQueue));
        }
        while (!blockingQueue.isEmpty()) {
            //Wait
        }
        long endTimeForPublishing = System.currentTimeMillis();
        System.out.println("Time taken by main thread to publish tasks: " + (endTimeForPublishing - startTime) + " ms");

        executorService.shutdown();

        double totalSum = 0;
        while (!workerResponseQueue.isEmpty()) {
            Double response = workerResponseQueue.pollResponse();
            if (response != null) {
                totalSum += response;
            }
        }

        System.out.println("Total sum of all worker responses: " + totalSum);
        long endTimeForProcessing = System.currentTimeMillis();
        System.out.println("Time taken by background process to finish: " + (endTimeForProcessing - startTime) + " ms");
        System.out.println("All tasks are processed!");
    }

    private static void assignmentPart4() {
        MyBlockingQueue<Integer> blockingQueue = new MyBlockingQueue<>(5);

        int numberOfWorkers = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfWorkers);

        long startTime = System.currentTimeMillis();
        for (int i = 100; i <= 10000; i += 100) {
            try {
                blockingQueue.add(i);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (int i = 0; i < numberOfWorkers; i++) {
            executorService.submit(new WorkerWithBlockingQueue(blockingQueue));
        }

        while (!blockingQueue.isEmpty()) {
            //Wait
        }

        executorService.shutdown();

        long endTimeForPublishing = System.currentTimeMillis();
        System.out.println("Time taken by main thread to publish tasks: " + (endTimeForPublishing - startTime) + " ms");
        while (!executorService.isTerminated()) {
            // Block until all workers finish
        }
        long endTimeForProcessing = System.currentTimeMillis();
        System.out.println("Time taken by background process to finish: " + (endTimeForProcessing - startTime) + " ms");
        System.out.println("All tasks are processed!");
    }

    private static void assignmentPart3() {
        MyPriorityQueue<Integer> priorityQueue = new MyPriorityQueue<>(10);

        int numberOfWorkers = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfWorkers);

        for (int i = 100; i <= 1000; i += 100) {
            int priority = (i % 200 == 0) ? 0 : 100;
            priorityQueue.add(i, priority);
        }

        for (int i = 0; i < numberOfWorkers; i++) {
            executorService.submit(new WorkerWithPriority(priorityQueue));
        }

        while (!priorityQueue.isEmpty()) {
            // Keep the main thread running while workers are processing
        }

        System.out.println("All tasks are processed!");

        long lastPriority100CompletionTime = WorkerWithPriority.getLastPriority100CompletionTime();
        long lastPriority0CompletionTime = WorkerWithPriority.getLastPriority0CompletionTime();

        if (lastPriority100CompletionTime != -1 && lastPriority0CompletionTime != -1) {
            System.out.println("Last priority 100 task completion time: " + lastPriority100CompletionTime + " ms");
            System.out.println("Last priority 0 task completion time: " + lastPriority0CompletionTime + " ms");

            long timeDifference = lastPriority0CompletionTime - lastPriority100CompletionTime;
            System.out.println("Priority 100 tasks were completed " + timeDifference + " ms faster than priority 0 tasks.");
        }
    }

    private static void assignmentPart1_2(String[] args) {
        MyQueue<Integer> queue = new MyQueue<>(100);

        int nThreads = args.length > 0 ? Integer.parseInt(args[0]) : 10;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        for(int i = 0; i < nThreads; i++) {
            executor.execute(new Worker(queue));
        }

        // Publish events to queue
        long start = Instant.now().toEpochMilli();
        for (int i = 100; i <= 10000; i += 100) {
            queue.add(i);
        }
        queue.endOfQueue();
        long endPublish = Instant.now().toEpochMilli();
        System.out.println("Main Thread end: Time taken to publish: " + (endPublish - start) + " ms");

        // Wait for executor to finish
        try {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long end = Instant.now().toEpochMilli();
        System.out.println("All tasks processed in " + (end - start) + " ms.");
    }
}
