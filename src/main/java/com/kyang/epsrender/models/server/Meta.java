package com.kyang.epsrender.models.server;

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


    //// SECTION: Getters and setters

    // SECTION: Job Queue
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

    public JobRequest getJobFromJobQueue() {
        for (JobRequest request : jobQueue) {
            if (isJobTakenByNode(request)) {
                return request;
            }
        }
        return null;
    }

    // SECTION: Verifying Queue
    public ArrayList<JobRequest> getVerifyingQueue() {
        return verifyingQueue;
    }

    public void setVerifyingQueue(ArrayList<JobRequest> verifyingQueue) {
        this.verifyingQueue = verifyingQueue;
    }

    public void addToVerifyingQueue(JobRequest jobRequest) {
        this.verifyingQueue.add(jobRequest);
    }

    public JobRequest getJobFromVerifyingQueue() {
        for (JobRequest request : verifyingQueue) {
            if (!isJobTakenByNode(request)) {
                return request;
            }
        }
        return null;
    }

    public JobRequest getJobFromVerifyingQueueWithName(String folderName) {
        return verifyingQueue.stream().filter(jobRequest -> folderName.equals(jobRequest.getProjectFolderName())).findFirst().orElse(null);
    }

    public void removeJobFromVerifyingQueueWithName(String folderName) {
        verifyingQueue.remove(verifyingQueue.stream().filter(jobRequest -> folderName.equals(jobRequest.getProjectFolderName())).findFirst().orElse(null));
    }

    // SECTION: Blender Queue
    public ArrayList<JobRequest> getBlenderQueue() {
        return blenderQueue;
    }

    public void setBlenderQueue(ArrayList<JobRequest> blenderQueue) {
        this.blenderQueue = blenderQueue;
    }

    public void addToBlenderQueue(JobRequest jobRequest) {
        this.blenderQueue.add(jobRequest);
    }

    public void removeBlenderJob(JobRequest blenderJob) {
        this.blenderQueue.remove(blenderJob);
    }

    // SECTION: Server Nodes
    public ArrayList<Node> getServerNodes() {
        sortServerNodes();
        return serverNodes;
    }

    public Node getServerNodeWithID(String ctxID) {
        return serverNodes.stream().filter(node -> ctxID.equals(node.getCtxSessionID())).findFirst().orElse(null);
    }

    public Node getServerNodeWithName(String nodeName) {
        return serverNodes.stream().filter(node -> nodeName.equals(node.getNodeName())).findFirst().orElse(null);
    }

    public Node getReadyServerNode() {
        return getServerNodes().stream().filter(node -> node.getNodeStatus().equals(NodeStatus.Ready)).findFirst().orElse(null);
    }

    public boolean isJobTakenByNode(JobRequest jobRequest) {
        for (Node node : serverNodes) {
            JobRequest currentJob = node.getCurrentJob();
            if (currentJob != null && currentJob.equals(jobRequest)) {
                return true;
            }
        }
        return false;
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

    private void sortServerNodes() {
        // sort nodes based on comparator
        if (serverNodes.size() > 1) {
            serverNodes.sort(new NodeSortingComparator());
        }
    }
}
