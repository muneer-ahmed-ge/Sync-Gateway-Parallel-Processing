package com.example.parallel.processing.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Node {

    public static int getTotalTime(String traceId, Map<String, Integer> data) {
        Node root = new Node(traceId, data.get(traceId + ":" + traceId), null);
        buildTree(root, data);
        return root.computeTotalTime();
    }

    private static final String SEPARATOR = ":";

    private final String traceId;
    private final Integer time;
    private final Node parent;
    private final List<Node> children = new ArrayList<>();

    private void addChild(String traceId, Integer time) {
        children.add(new Node(traceId, time, this));
    }

    private int computeTotalTime() {
        int max = 0;
        for (Node n : children) {
            max = Math.max(max, n.computeTotalTime());
        }
        return time + max;
    }

    private static void buildTree(Node node, Map<String, Integer> data) {
        data.keySet().stream().filter(e -> e.startsWith(node.getTraceId() + ":")).filter(e -> !e.equals(node.getTraceId() + ":" + node.getTraceId())).forEach(e -> node.addChild(e.split(":")[1], data.get(e.split(":")[1] + ":" + e.split(":")[1])));
        node.getChildren().stream().forEach(n -> buildTree(n, data));
    }

}
