package com.kyang.epsrender.models.server;

import com.kyang.epsrender.models.server.Node;

import java.util.Comparator;

public class NodeSortingComparator implements Comparator<Node> {

    @Override
    public int compare(Node node1, Node node2) {
        // gather compares
        int statusComp = node1.getNodeStatus().compareTo(node2.getNodeStatus());
        int powerComp = node1.getPowerIndex().compareTo(node2.getPowerIndex());

        if (statusComp == 0) {
            return powerComp;
        }
        return statusComp;
    }
}
