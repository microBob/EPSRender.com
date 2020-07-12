package com.kyang.eprender.models;

import com.kyang.eprender.EPRenderCore;
import com.kyang.eprender.Enums.NodeStatus;
import com.kyang.eprender.Enums.PowerIndex;

public class Node {
    // SECTION: Properties
    private String nodeName;
    private String ipAddress;
    private PowerIndex powerIndex;
    private NodeStatus nodeStatus = NodeStatus.Ready;
    private JobRequest currentJob;


    // SECTION: Constructors
    // registering a new node
    public Node(String nodeName, String ipAddress, int powerIndex) {
        this.nodeName = nodeName;
        this.ipAddress = ipAddress;
        this.powerIndex = PowerIndex.values()[powerIndex];
    }


    // SECTION: Internal functions
    public void getNextJob() {
        Meta serverMeta = EPRenderCore.getServerMeta();

        // TODO: pop next job from action and handle get from blender if nothing in action
    }

    // SECTION: Delegate Methods
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public PowerIndex getPowerIndex() {
        return powerIndex;
    }

    public void setPowerIndex(PowerIndex powerIndex) {
        this.powerIndex = powerIndex;
    }

    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public JobRequest getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(JobRequest currentJob) {
        this.currentJob = currentJob;
    }
}
