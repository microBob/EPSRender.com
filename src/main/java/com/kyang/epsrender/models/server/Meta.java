package com.kyang.epsrender.models.server;

import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.Enums.ProjectType;
import com.kyang.epsrender.models.messages.JobRequest;
import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.HashMap;

public class Meta {
    private final ArrayList<JobRequest> jobQueue = new ArrayList<>();
    private final ArrayList<JobRequest> verifyingQueue = new ArrayList<>();
    private final ArrayList<JobRequest> blenderQueue = new ArrayList<>();
    private final ArrayList<Node> serverNodes = new ArrayList<>();
    private final HashMap<String, WsContext> ctxIdHash = new HashMap<>();


    //// SECTION: Getters and setters

    // SECTION: Job Queue
    public ArrayList<JobRequest> getJobQueue() {
        synchronized (jobQueue) {
            return jobQueue;
        }
    }

    public void addToJobQueue(JobRequest jobRequest) {
        synchronized (jobQueue) {
            this.jobQueue.add(jobRequest);
        }
    }

    public void addToJobQueueBeginning(JobRequest jobRequest) {
        synchronized (jobQueue) {
            this.jobQueue.add(0, jobRequest);
        }
    }

    public JobRequest getJobFromJobQueue() {
        synchronized (jobQueue) {
            // check for necro
            JobRequest jobZero = jobQueue.get(0);
            if (jobZero != null) {
                if (jobZero.getJobStatus().equals(JobStatus.Necro)) {
                    jobZero.setJobStatus(JobStatus.Rendering);
                    jobQueue.set(0, jobZero);
                    return jobZero;
                }
            }
            // check for active blender jobs
            JobRequest openBlenderJob =
                    jobQueue.stream().filter(jobRequest -> jobRequest.getBlenderInfo() != null && jobRequest.getBlenderInfo().getRenderers() == 0).findFirst().orElse(null);
            if (openBlenderJob != null) {
                openBlenderJob.getBlenderInfo().setRenderers(1);
                return openBlenderJob;
            }

            for (JobRequest request : jobQueue) {
                if (isJobTakenByNode(request)) {
                    return request;
                }
            }
            return null;
        }
    }

    // SECTION: Verifying Queue
    public ArrayList<JobRequest> getVerifyingQueue() {
        synchronized (verifyingQueue) {
            return verifyingQueue;
        }
    }

    public void addToVerifyingQueue(JobRequest jobRequest) {
        synchronized (verifyingQueue) {
            this.verifyingQueue.add(jobRequest);
        }
    }

    public JobRequest getJobFromVerifyingQueue() {
        synchronized (verifyingQueue) {
            for (JobRequest request : verifyingQueue) {
                if (!isJobTakenByNode(request)) {
                    return request;
                }
            }
            return null;
        }
    }

    public JobRequest getJobFromVerifyingQueueWithName(String folderName) {
        synchronized (verifyingQueue) {
            return verifyingQueue.stream().filter(jobRequest -> folderName.equals(jobRequest.getProjectFolderName())).findFirst().orElse(null);
        }
    }

    public void removeJobFromVerifyingQueueWithName(String folderName) {
        synchronized (verifyingQueue) {
            verifyingQueue.remove(verifyingQueue.stream().filter(jobRequest -> folderName.equals(jobRequest.getProjectFolderName())).findFirst().orElse(null));
        }
    }

    // SECTION: Blender Queue
    public ArrayList<JobRequest> getBlenderQueue() {
        synchronized (blenderQueue) {
            return blenderQueue;
        }
    }

    public void addToBlenderQueue(JobRequest jobRequest) {
        synchronized (blenderQueue) {
            this.blenderQueue.add(jobRequest);
        }
    }

    public void removeBlenderJob(JobRequest blenderJob) {
        synchronized (blenderQueue) {
            this.blenderQueue.remove(blenderJob);
        }
    }

    // SECTION: Server Nodes
    public ArrayList<Node> getServerNodes() {
        sortServerNodes();
        synchronized (serverNodes) {
            return serverNodes;
        }
    }

    public Node getServerNodeWithID(String ctxID) {
        synchronized (serverNodes) {
            return serverNodes.stream().filter(node -> ctxID.equals(node.getCtxSessionID())).findFirst().orElse(null);
        }
    }

    public Node getServerNodeWithName(String nodeName) {
        synchronized (serverNodes) {
            return serverNodes.stream().filter(node -> nodeName.equals(node.getNodeName())).findFirst().orElse(null);
        }
    }

    public Node getReadyServerNode() {
        synchronized (serverNodes) {
            return getServerNodes().stream().filter(node -> node.getNodeStatus().equals(NodeStatus.Ready)).findFirst().orElse(null);
        }
    }

    public boolean isJobTakenByNode(JobRequest jobRequest) {
        synchronized (serverNodes) {
            for (Node node : serverNodes) {
                JobRequest currentJob = node.getCurrentJob();
                if (currentJob != null && currentJob.equals(jobRequest)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void addServerNode(Node serverNode) {
        synchronized (serverNodes) {
            this.serverNodes.add(serverNode);
        }
    }

    public void removeServerNode(Node serverNode) {
        synchronized (serverNodes) {
            this.serverNodes.remove(serverNode);
        }
    }

    public HashMap<String, WsContext> getCtxIdHash() {
        synchronized (ctxIdHash) {
            return ctxIdHash;
        }
    }

    public void addToCtxIdHash(String id, WsContext ctx) {
        synchronized (ctxIdHash) {
            this.ctxIdHash.put(id, ctx);
        }
    }


    // SECTION: internal methods
    private boolean nodesAvailable() {
        synchronized (serverNodes) {
            return serverNodes.stream().anyMatch(node -> node.getNodeStatus().equals(NodeStatus.Ready));
        }
    }

    private void sortServerNodes() {
        // sort nodes based on comparator
        synchronized (serverNodes) {
            if (serverNodes.size() > 1) {
                serverNodes.sort(new NodeSortingComparator());
            }
        }
    }
}
