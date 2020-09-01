package com.kyang.epsrender.models.server;

import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.models.messages.JobRequest;
import io.javalin.websocket.WsContext;
import org.jetbrains.annotations.Nullable;

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
//            System.out.println("Looking for job");
            if (!jobQueue.isEmpty()) {
                // check for necro
//                System.out.println("checking for necro");

                JobRequest jobZero = jobQueue.get(0);
                if (jobZero.getJobStatus().equals(JobStatus.Necro)) {
                    jobZero.setJobStatus(JobStatus.Rendering);
                    getJobQueue().set(0, jobZero);
                    return jobZero;
                }

                // check for active blender jobs
//                System.out.println("Checking for already open blender");
                JobRequest openBlenderJob =
                        jobQueue.stream().filter(jobRequest -> jobRequest.getBlenderInfo() != null &&
                                jobRequest.getJobStatus().equals(JobStatus.Rendering) &&
                                jobRequest.getBlenderInfo().getRenderers() == 0).findFirst().orElse(null);
//                int index = jobQueue.indexOf(openBlenderJob);
//                JobRequest nextFrame = getNextFrameOfBlenderJob(openBlenderJob/*, index*/);
//                if (nextFrame != null) return nextFrame;
                if (openBlenderJob != null) {
                    JobRequest nextFrame = getJobFromBlenderQueueWithName(openBlenderJob.getProjectFolderName());
//            int frameIndex = getBlenderQueue().indexOf(nextFrame);
                    nextFrame.setJobStatus(JobStatus.Rendering);
                    openBlenderJob.getBlenderInfo().addRenderers();
//            getJobQueue().set(index, openBlenderJob);
//            getBlenderQueue().set(frameIndex, nextFrame);
//                    System.out.println("Active next frame "+nextFrame.getBlenderInfo().getFrameNumber());
                    return nextFrame;
                }

                // pick next job
//                System.out.println("checking for new ME");
                JobRequest openJob =
                        jobQueue.stream().filter(jobRequest -> isJobNotTakenByNode(jobRequest)).findFirst().orElse(null);
//                index = jobQueue.indexOf(openJob);
                if (openJob != null) {
                    // pick next frame from active blender job
                    if (openJob.getBlenderInfo() != null) {
                        JobRequest nextFrame = getJobFromBlenderQueueWithName(openJob.getProjectFolderName());
//            int frameIndex = getBlenderQueue().indexOf(nextFrame);
                        if (nextFrame != null) {
                            nextFrame.setJobStatus(JobStatus.Rendering);
                            openJob.getBlenderInfo().addRenderers();
                        }
//            getJobQueue().set(index, openBlenderJob);
//            getBlenderQueue().set(frameIndex, nextFrame);
//                    System.out.println("Passive next frame "+nextFrame.getBlenderInfo().getFrameNumber());
                        return nextFrame;
                    }
                    openJob.setJobStatus(JobStatus.Rendering);
//                    jobQueue.set(index, openJob);
                    return openJob;
                }
            }
            return null;
        }
    }

    public JobRequest getJobFromJobQueueWithName(String folderName) {
        synchronized (jobQueue) {
            return this.jobQueue.stream().filter(jobRequest -> jobRequest.getProjectFolderName().equals(folderName)).findFirst().orElse(null);
        }
    }

    public void removeJobFromJobQueWithName(String folderName) {
        synchronized (jobQueue) {
            this.jobQueue.removeIf(jobRequest -> jobRequest.getProjectFolderName().equals(folderName));
        }
    }

    // SECTION: Verifying Queue

    public ArrayList<JobRequest> getVerifyingQueue() {
        synchronized (verifyingQueue) {
            return this.verifyingQueue;
        }
    }

    public void addToVerifyingQueue(JobRequest jobRequest) {
        synchronized (verifyingQueue) {
            this.verifyingQueue.add(jobRequest);
        }
    }

    public JobRequest getJobFromVerifyingQueue() {
        synchronized (verifyingQueue) {
            return this.verifyingQueue.stream().filter(jobRequest -> isJobNotTakenByNode(jobRequest)).findFirst().orElse(null);
        }
    }

    public JobRequest getJobFromVerifyingQueueWithName(String folderName) {
        synchronized (verifyingQueue) {
            return this.verifyingQueue.stream().filter(jobRequest -> folderName.equals(jobRequest.getProjectFolderName())).findFirst().orElse(null);
        }
    }

    public void removeJobFromVerifyingQueueWithName(String folderName) {
        synchronized (verifyingQueue) {
            this.verifyingQueue.removeIf(jobRequest -> jobRequest.getProjectFolderName().equals(folderName));
        }
    }

    // SECTION: Blender Queue

    public ArrayList<JobRequest> getBlenderQueue() {
        synchronized (blenderQueue) {
            return this.blenderQueue;
        }
    }

    public JobRequest getJobFromBlenderQueueWithName(String folderName) {
        synchronized (blenderQueue) {
            return this.blenderQueue.stream().filter(jobRequest -> jobRequest.getProjectFolderName().equals(folderName) && isJobNotTakenByNode(jobRequest)).findFirst().orElse(null);
        }
    }

    @Nullable
    private JobRequest getNextFrameOfBlenderJob(JobRequest openBlenderJob/*, int index*/) {
        if (openBlenderJob != null) {
            JobRequest nextFrame = getJobFromBlenderQueueWithName(openBlenderJob.getProjectFolderName());
//            int frameIndex = getBlenderQueue().indexOf(nextFrame);
            nextFrame.setJobStatus(JobStatus.Rendering);
            openBlenderJob.getBlenderInfo().addRenderers();
//            getJobQueue().set(index, openBlenderJob);
//            getBlenderQueue().set(frameIndex, nextFrame);
            return nextFrame;
        }
        return null;
    }

    public void addToBlenderQueue(JobRequest jobRequest) {
        synchronized (blenderQueue) {
            this.blenderQueue.add(jobRequest);
        }
    }

    public void removeBlenderJobWithNameAndFrame(String folderName, int frame) {
        synchronized (blenderQueue) {
            this.blenderQueue.removeIf(jobRequest -> jobRequest.getProjectFolderName().equals(folderName) && jobRequest.getBlenderInfo().getFrameNumber() == frame);
        }
    }

    public void removeBlenderJobWithName(String folderName) {
        synchronized (blenderQueue) {
            this.blenderQueue.removeIf(jobRequest -> jobRequest.getProjectFolderName().equals(folderName));
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
