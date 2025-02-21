package org.nash.eymultithreading;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DAGManager implements Runnable {
    private DAGNode dag;
    private MyBlockingQueue<String> requestQueue;
    private MyBlockingQueue<Double> responseQueue;
    private MyBlockingQueue<Node> workerQueue;
    private MyBlockingQueue<Node> workerResponseQueue;
    private int batchCount;

    public DAGManager(MyBlockingQueue<String> requestQueue, MyBlockingQueue<Double> responseQueue,
                   MyBlockingQueue<Node> workerQueue, MyBlockingQueue<Node> workerResponseQueue) {
        this.requestQueue = requestQueue;
        this.responseQueue = responseQueue;
        this.workerQueue = workerQueue;
        this.workerResponseQueue = workerResponseQueue;
        this.batchCount = 0;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String equation = requestQueue.remove();
                if (equation == null)
                    break;

                DAGNode dag = parseEquation(equation);
                double result = executeDAG(dag);
                responseQueue.add(result);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private DAGNode parseEquation(String equation) {
        this.dag = new DAGNode();
        parseExpression(equation, dag, new AtomicInteger(1));
        return dag;
    }

    private String parseExpression(String expression, DAGNode dag, AtomicInteger nodeIdCounter) {
        expression = expression.replaceAll("\\s+", "");

        if (expression.matches("\\d+(\\.\\d+)?")) {
            return expression;
        }

        while (expression.contains("(")) {
            int openBracketIndex = expression.lastIndexOf("(");
            int closeBracketIndex = findMatchingCloseBracket(expression, openBracketIndex);

            // Extract the sub-expression inside the brackets
            String subExpression = expression.substring(openBracketIndex + 1, closeBracketIndex);

            // Recursively parse the sub-expression
            String subResult = parseExpression(subExpression, dag, nodeIdCounter);

            // Replace the bracketed sub-expression with its result
            expression = expression.substring(0, openBracketIndex) + subResult + expression.substring(closeBracketIndex + 1);
        }

        // Handle multiplication and division
        expression = handleOperators(expression, dag, nodeIdCounter, new String[]{"*", "/"});

        // Handle addition and subtraction
        expression = handleOperators(expression, dag, nodeIdCounter, new String[]{"+", "-"});

        return expression;
    }

    private String handleOperators(String expression, DAGNode dag, AtomicInteger nodeIdCounter, String[] operators) {
        for (String operator : operators) {
            while (expression.contains(operator)) {
                int operatorIndex = expression.indexOf(operator);

                String leftOperand = findLeftOperand(expression, operatorIndex);
                String rightOperand = findRightOperand(expression, operatorIndex);

                // Create a new node for the operation
                String nodeId = "Node" + nodeIdCounter.getAndIncrement();
                String calculation = leftOperand + operator + rightOperand;
                dag.addNode(nodeId, calculation);

                // Add dependencies if the operands are nodes
                if (leftOperand.startsWith("Node")) {
                    dag.addDependency(leftOperand, nodeId);
                }
                if (rightOperand.startsWith("Node")) {
                    dag.addDependency(rightOperand, nodeId);
                }

                // Replace the operation with the new node
                expression = expression.replace(leftOperand + operator + rightOperand, nodeId);
            }
        }
        return expression;
    }

    private String findLeftOperand(String expression, int operatorIndex) {
        int startIndex = operatorIndex - 1;
        while (startIndex >= 0 && (Character.isDigit(expression.charAt(startIndex)) || expression.charAt(startIndex) == '.')) {
            startIndex--;
        }
        return expression.substring(startIndex + 1, operatorIndex);
    }

    private String findRightOperand(String expression, int operatorIndex) {
        int endIndex = operatorIndex + 1;
        while (endIndex < expression.length() && (Character.isDigit(expression.charAt(endIndex)) || expression.charAt(endIndex) == '.')) {
            endIndex++;
        }
        return expression.substring(operatorIndex + 1, endIndex);
    }

    private int findMatchingCloseBracket(String expression, int openBracketIndex) {
        int bracketCount = 1;
        for (int i = openBracketIndex + 1; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                bracketCount++;
            } else if (expression.charAt(i) == ')') {
                bracketCount--;
                if (bracketCount == 0) {
                    return i;
                }
            }
        }
        throw new IllegalArgumentException("Mismatched brackets in expression: " + expression);
    }

    private double executeDAG(DAGNode dag) throws InterruptedException {
        while (true) {
            List<Node> readyNodes = dag.getReadyNodes();
            if (readyNodes.isEmpty())
                break;

            for (Node node : readyNodes) {
                workerQueue.add(node);
            }
            batchCount++;

            for (Node node : readyNodes) {
                Node completedNode = workerResponseQueue.remove();
                node.result = completedNode.result;
            }
        }

        // Return the final result
        return dag.nodes.get(batchCount - 1).result;
    }

    public int getBatchCount() {
        return batchCount;
    }

    public DAGNode getDag() {
        return dag;
    }
}
