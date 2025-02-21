package org.nash.eymultithreading;

import java.util.*;

public class DAGNode {
    Map<String, Node> nodes;
    List<Node> roots;

    public DAGNode() {
        this.nodes = new HashMap<>();
        this.roots = new ArrayList<>();
    }

    public void addNode(String id, String calculation) {
        Node node = new Node(id, calculation);
        nodes.put(id, node);
        roots.add(node);
    }

    public void addDependency(String parentId, String childId) {
        Node parent = nodes.get(parentId);
        Node child = nodes.get(childId);
        child.addDependency(parent);
        roots.remove(child);
    }

    public List<Node> getReadyNodes() {
        List<Node> readyNodes = new ArrayList<>();
        for (Node node : nodes.values()) {
            if (node.isReady()) {
                readyNodes.add(node);
            }
        }
        return readyNodes;
    }

    public int getNumberOfNodes() {
        return nodes.size();
    }
}

