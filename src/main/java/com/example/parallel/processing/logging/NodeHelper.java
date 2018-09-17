package com.example.parallel.processing.logging;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class NodeHelper {

    private static final String SEPARATOR = "/";

    public static long getTotalTime(String traceId, Map<String, Long> data) {
        Node root = new Node(traceId, data.get(traceId + SEPARATOR + traceId), null);
        data.remove(traceId + SEPARATOR + traceId);
        buildTree(root, data);
        List<String> keys = new ArrayList<>();
        childrenKeys(root, keys);
        keys.stream().forEach(data::remove);
        return root.getTotalTime();
    }

    private static void buildTree(Node node, Map<String, Long> data) {
        data.keySet().stream().filter(e -> e.startsWith(node.getTraceId() + SEPARATOR)).
                forEach(e -> node.addChild(StringUtils.substringAfter(e, SEPARATOR), data.get(e)));
        node.getChildren().stream().forEach(n -> buildTree(n, data));
    }

    private static void childrenKeys(Node n, List<String> keys) {
        keys.add((n.getParent() == null ? "" : n.getParent().getTraceId()) + SEPARATOR + n.getTraceId());
        n.getChildren().stream().forEach(e -> childrenKeys(e, keys));
    }
}
