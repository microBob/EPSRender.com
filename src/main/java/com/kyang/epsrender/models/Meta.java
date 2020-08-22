package com.kyang.epsrender.models;

import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.Enums.ProjectType;

import java.util.ArrayList;

public class Meta {
    private ArrayList<JobRequest> jobQueue = new ArrayList<>();
    private ArrayList<JobRequest> verifyingQueue = new ArrayList<>();
    private ArrayList<JobRequest> blenderQueue = new ArrayList<>();
    private ArrayList<Node> serverNodes = new ArrayList<>();


    // SECTION: Getters and setters

    public ArrayList<JobRequest> getJobQueue() {
        return jobQueue;
    }

    public void setJobQueue(ArrayList<JobRequest> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public void addToJobQueue(JobRequest jobRequest) {
        jobRequest.setJobStatus(JobStatus.Queued);
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
        for (Node n : serverNodes) {
            if (n.getCtxSessionID().equals(ctxID)) {
                return n;
            }
        }

        return null;
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
