package com.kyang.epsrender.models.server;

import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.models.messages.JobRequest;
import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.HashMap;

public class Meta {
    private ArrayList<JobRequest> jobQueue = new ArrayList<>();
    private ArrayList<JobRequest> verifyingQueue = new ArrayList<>();
    private ArrayList<JobRequest> blenderQueue = new ArrayList<>();
    private ArrayList<Node> serverNodes = new ArrayList<>();
    private HashMap<String, WsContext> ctxIdHash = new HashMap<>();


    // SECTION: Getters and setters

    public ArrayList<JobRequest> getJobQueue() {
        return jobQueue;
    }

    public void setJobQueue(ArrayList<JobRequest> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public void addToJobQueue(JobRequest jobRequest) {
        this.jobQueue.add(jobRequest);
    }

    public void addToJobQueueBeginning(JobRequest jobRequest) {
        this.jobQueue.add(0, jobRequest);
    }

    public ArrayList<JobRequest> getVerifyingQueue() {
        return verifyingQueue;
    }

    public void setVerifyingQueue(ArrayList<JobRequest> verifyingQueue) {
        this.verifyingQueue = verifyingQueue;
    }

    public void addToVerifyingQueue(JobRequest jobRequest) {
        this.verifyingQueue.add(jobRequest);
    }

    public ArrayList<JobRequest> getBlenderQueue() {
        return blenderQueue;
    }

    public void setBlenderQueue(ArrayList<JobRequest> blenderQueue) {
        this.blenderQueue = blenderQueue;
    }

    public void removeBlenderJob(JobRequest blenderJob) {
        this.blenderQueue.remove(blenderJob);
    }

    public ArrayList<Node> getServerNodes() {
        return serverNodes;
    }

    public Node getServerNodeWithID(String ctxID) {
        return serverNodes.stream().filter(n -> ctxID.equals(n.getCtxSessionID())).findFirst().orElse(null);
    }

    public Node getServerNodeWithName(String nodeName) {
        return serverNodes.stream().filter(n -> nodeName.equals(n.getNodeName())).findFirst().orElse(null);
    }

    public void setServerNodes(ArrayList<Node> serverNodes) {
        this.serverNodes = serverNodes;
    }

    public void addServerNode(Node serverNode) {
        this.serverNodes.add(serverNode);
    }

    public void removeServerNode(Node serverNode) {
        this.serverNodes.remove(serverNode);
    }

    public HashMap<String, WsContext> getCtxIdHash() {
        return ctxIdHash;
    }

    public void setCtxIdHash(HashMap<String, WsContext> ctxIdHash) {
        this.ctxIdHash = ctxIdHash;
    }

    public void addToCtxIdHash(String id, WsContext ctx) {
        this.ctxIdHash.put(id, ctx);
    }


    // SECTION: internal methods

    private boolean nodesAvailable() {
        if (getJobQueue().size() == 0) {
            for (Node node : getServerNodes()) {
                if (node.getNodeStatus().equals(NodeStatus.Ready)) {
                    return true;
                }
            }
        }
        return false;
    }
}
