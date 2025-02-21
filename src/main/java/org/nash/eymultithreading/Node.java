package org.nash.eymultithreading;

import java.util.ArrayList;
import java.util.List;

public class Node {
    String id;
    String calculation;
    List<Node> dependencies;
    double result;

    public Node(String id, String calculation) {
        this.id = id;
        this.calculation = calculation;
        this.dependencies = new ArrayList<>();
        this.result = Double.NaN;
    }

    public void addDependency(Node node) {
        dependencies.add(node);
    }

    public boolean isReady() {
        // A node is ready if all its dependencies have results
        for (Node dependency : dependencies) {
            if (Double.isNaN(dependency.result)) {
                return false;
            }
        }
        return true;
    }

    public double execute() {
        if (calculation.contains("+")) {
            String[] parts = calculation.split("\\+");
            return Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
        } else if (calculation.contains("*")) {
            String[] parts = calculation.split("\\*");
            return Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
        } else if (calculation.contains("/")) {
            String[] parts = calculation.split("/");
            return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
        } else {
            return Double.parseDouble(calculation);
        }
    }
}
