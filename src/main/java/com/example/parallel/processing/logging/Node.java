package com.example.parallel.processing.logging;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Node {

    private final String traceId;
    private final Long time;
    private final Node parent;
    private final List<Node> children = new ArrayList<>();

    public void addChild(String traceId, Long time) {
        children.add(new Node(traceId, time, this));
    }

    public Long getTotalTime() {
        long max = 0;
        for (Node n : children) {
            max = Math.max(max, n.getTotalTime());
        }
        return time + max;
    }

}