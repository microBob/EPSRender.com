package com.kyang.eprender.models;

import com.kyang.eprender.EPRenderCore;
import com.kyang.eprender.Enums.NodeStatus;
import com.kyang.eprender.Enums.ProjectType;

import java.util.ArrayList;

public class Meta {
    private ArrayList<JobRequest> jobQueue = new ArrayList<>();
    private ArrayList<JobRequest> actionQueue = new ArrayList<>();
    private ArrayList<JobRequest> blenderJobs = new ArrayList<>();
    private ArrayList<Node> serverNodes = new ArrayList<>();

    public ArrayList<JobRequest> getJobQueue() {
        return jobQueue;
    }

    public void setJobQueue(ArrayList<JobRequest> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public void addToJobQueue(JobRequest jobRequest) {
        this.jobQueue.add(jobRequest);
    }

    public ArrayList<JobRequest> getActionQueue() {
        return actionQueue;
    }

    public void setActionQueue(ArrayList<JobRequest> actionQueue) {
        this.actionQueue = actionQueue;
    }

    public void addToActionQueue(JobRequest jobRequest) {
        this.actionQueue.add(jobRequest);

        // TODO: needs testing and documentation
        
        Meta serverMeta = EPRenderCore.getServerMeta();
        for (Node node : serverMeta.getServerNodes()) {
            if (node.getNodeStatus().equals(NodeStatus.Ready)) {
                if (serverMeta.getActionQueue().size() > 0) {
                    node.setCurrentJob(serverMeta.popActionQueue());
                } else {
                    if (serverMeta.blenderJobs.size() > 0) {
                        ArrayList<JobRequest> openBlenderJobs = new ArrayList<>();
                        for (JobRequest job : serverMeta.jobQueue) {
                            if (job.getProjectType().compareTo(ProjectType.AfterEffects) > 0) {
                                openBlenderJobs.add(job);
                            }
                        }

                        JobRequest lowestCount = openBlenderJobs.get(0);
                        if (openBlenderJobs.size() > 1) {
                            for (JobRequest job : openBlenderJobs) {
                                if (job.equals(openBlenderJobs.get(0))) {
                                    continue;
                                }
                                if (job.getBlenderDistributedAmount() < lowestCount.getBlenderDistributedAmount()) {
                                    lowestCount = job;
                                }
                            }
                        }

                        for (JobRequest blenderJob : serverMeta.blenderJobs) {
                            if (blenderJob.getProjectFile().equals(lowestCount.getProjectFile())) {
                                serverMeta.addToActionQueue(blenderJob);
                                serverMeta.popBlenderJob(blenderJob);
                            }
                        }
                    }
                }
            }
        }
    }

    public JobRequest popActionQueue() {
        JobRequest first = this.actionQueue.get(0);
        this.actionQueue.remove(0);
        return first;
    }

    public ArrayList<JobRequest> getBlenderJobs() {
        return blenderJobs;
    }

    public void setBlenderJobs(ArrayList<JobRequest> blenderJobs) {
        this.blenderJobs = blenderJobs;
    }

    public void addToBlenderJobs(JobRequest jobRequest) {
        this.blenderJobs.add(jobRequest);
    }

    public void popBlenderJob(JobRequest blenderJob) {
        this.blenderJobs.remove(blenderJob);
    }

    public ArrayList<Node> getServerNodes() {
        return serverNodes;
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
}
