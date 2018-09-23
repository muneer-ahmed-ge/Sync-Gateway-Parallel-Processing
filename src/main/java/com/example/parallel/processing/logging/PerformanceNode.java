package com.example.parallel.processing.logging;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * An N-Array Tree to capture the Performance Metrics
 *
 * @author Muneer Ahmed
 * @version 1.0
 * @since 2018-09-10
 */
@Getter
@Setter
@AllArgsConstructor
class PerformanceNode {

    private final String traceId;
    private final Long time;
    private final PerformanceNode parent;
    private final List<PerformanceNode> children = new ArrayList<>();

    void addChild(String traceId, Long time) {
        children.add(new PerformanceNode(traceId, time, this));
    }

    public Long getTotalTime() {
        long max = 0;
        for (PerformanceNode n : children) {
            max = Math.max(max, n.getTotalTime());
        }
        return (time == null ? 0L : time) + max;
    }

}