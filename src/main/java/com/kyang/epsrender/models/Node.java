package com.kyang.epsrender.models;

import com.kyang.epsrender.EPSRenderCore;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.Enums.PowerIndex;

public class Node {
    // SECTION: Properties
    private String nodeName;
    private String ipAddress;
    private String ctxSessionID;
    private PowerIndex powerIndex;
    private NodeStatus nodeStatus = NodeStatus.Ready;
    private JobRequest currentJob;


    // SECTION: Constructors
    // registering a new node
    public Node(String nodeName, String ipAddress, String ctxSessionID, int powerIndex) {
        this.nodeName = nodeName;
        this.ipAddress = ipAddress;
        this.ctxSessionID = ctxSessionID;
        this.powerIndex = PowerIndex.values()[powerIndex];
    }


    // SECTION: Internal functions
    public void getNextJob() {
        Meta serverMeta = EPSRenderCore.getServerMeta();

        // TODO: pop next job from action and handle get from blender if nothing in action
    }

    // SECTION: Getters and setters
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

    public String getCtxSessionID() {
        return ctxSessionID;
    }

    public void setCtxSessionID(String ctxSessionID) {
        this.ctxSessionID = ctxSessionID;
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
