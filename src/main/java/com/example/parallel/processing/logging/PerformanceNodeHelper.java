package com.example.parallel.processing.logging;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper class to compute the total time for an N-Array Tree
 *
 * @author Muneer Ahmed
 * @version 1.0
 * @since 2018-09-10
 */

public class PerformanceNodeHelper {

    private static final String SEPARATOR = "/";

    public static long getTotalTime(String traceId, Map<String, Long> data) {
        StringBuilder rootKey = new StringBuilder(traceId);
        rootKey.append(SEPARATOR).append(traceId);
        PerformanceNode root = new PerformanceNode(traceId, data.get(rootKey.toString()), null);
        data.remove(rootKey.toString());
        buildTree(root, data);
        List<String> keys = new ArrayList<>();
        childrenKeys(root, keys);
        keys.stream().forEach(data::remove);
        return root.getTotalTime();
    }

    private static void buildTree(PerformanceNode node, Map<String, Long> data) {
        data.keySet().stream().filter(e -> e.startsWith(node.getTraceId() + SEPARATOR)).
                forEach(e -> node.addChild(StringUtils.substringAfter(e, SEPARATOR), data.get(e)));
        node.getChildren().stream().forEach(n -> buildTree(n, data));
    }

    private static void childrenKeys(PerformanceNode n, List<String> keys) {
        keys.add((n.getParent() == null ? "" : n.getParent().getTraceId()) + SEPARATOR + n.getTraceId());
        n.getChildren().stream().forEach(e -> childrenKeys(e, keys));
    }
}
