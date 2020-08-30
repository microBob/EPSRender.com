package com.kyang.epsrender.models.server;

import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.models.messages.JobRequest;
import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
                    jobQueue.stream().filter(jobRequest -> jobRequest.getBlenderInfo() != null &&
                            jobRequest.getJobStatus().equals(JobStatus.Rendering) &&
                            jobRequest.getBlenderInfo().getRenderers() == 0).findFirst().orElse(null);
            int index = jobQueue.indexOf(openBlenderJob);
            if (openBlenderJob != null) {
                openBlenderJob.getBlenderInfo().addRenderers();
                jobQueue.set(index, openBlenderJob);
                return openBlenderJob;
            }
            // pick next job
            JobRequest openJob =
                    jobQueue.stream().filter(jobRequest -> isJobNotTakenByNode(jobRequest)).findFirst().orElse(null);
            index = jobQueue.indexOf(openJob);
            if (openJob != null) {
                openJob.setJobStatus(JobStatus.Rendering);
                jobQueue.set(index, openJob);
                return openJob;
            }
            // pick next frame from active blender job
            for (JobRequest blenderJob :
                    jobQueue.stream().filter(jobRequest -> jobRequest.getBlenderInfo() != null && jobRequest.getJobStatus().equals(JobStatus.Rendering)).collect(Collectors.toList())) {
                index = jobQueue.indexOf(blenderJob);
                List<JobRequest> frames = getBlenderQueue().stream().filter(blenderFrame -> blenderFrame.getProjectFolderName().equals(blenderJob.getProjectFolderName())).collect(Collectors.toList());

                JobRequest openFrame =
                        frames.stream().filter(frame -> isJobNotTakenByNode(frame)).findFirst().orElse(null);
                int frameIndex = getBlenderQueue().indexOf(openFrame);
                if (openFrame != null) {
                    openFrame.setJobStatus(JobStatus.Rendering);
                    blenderJob.getBlenderInfo().addRenderers();

                    jobQueue.set(index, blenderJob);
                    getBlenderQueue().set(frameIndex, openFrame);

                    return openFrame;
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
            return verifyingQueue.stream().filter(jobRequest -> isJobNotTakenByNode(jobRequest)).findFirst().orElse(null);
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

    public boolean isJobNotTakenByNode(JobRequest jobRequest) {
        synchronized (serverNodes) {
            return serverNodes.stream().noneMatch(node -> node.getCurrentJob() != null && node.getCurrentJob().equals(jobRequest));
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
