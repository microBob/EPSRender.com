package com.kyang.epsrender.models.server;

import com.kyang.epsrender.EPSRenderCore;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.Enums.PowerIndex;
import com.kyang.epsrender.models.messages.JobRequest;
import com.kyang.epsrender.models.server.Meta;

public class Node {
    // SECTION: Properties
    private String nodeName;
    private String ctxSessionID;
    private PowerIndex powerIndex;
    private NodeStatus nodeStatus = NodeStatus.Ready;
    private JobRequest currentJob;


    // SECTION: Constructors
    public Node(String nodeName, String ctxSessionID, int powerIndex) {
        this.nodeName = nodeName;
        this.ctxSessionID = ctxSessionID;
        this.powerIndex = PowerIndex.values()[powerIndex];
    }

    public Node(String ctxSessionID) {
        this.ctxSessionID = ctxSessionID;
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
