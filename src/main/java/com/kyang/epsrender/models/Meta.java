package com.kyang.epsrender.models;

import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.Enums.ProjectType;

import java.util.ArrayList;

public class Meta {
    private ArrayList<JobRequest> jobQueue = new ArrayList<>();
    private ArrayList<JobRequest> actionQueue = new ArrayList<>();
    private ArrayList<JobRequest> blenderJobs = new ArrayList<>();
    private ArrayList<Node> serverNodes = new ArrayList<>();


    // SECTION: delegate methods

    public ArrayList<JobRequest> getJobQueue() {
        return jobQueue;
    }

    public void setJobQueue(ArrayList<JobRequest> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public void addToJobQueue(JobRequest jobRequest) {
        jobRequest.setStatus(JobStatus.Queued);
        this.jobQueue.add(jobRequest);
    }

    public ArrayList<JobRequest> getActionQueue() {
        return actionQueue;
    }

    public void setActionQueue(ArrayList<JobRequest> actionQueue) {
        this.actionQueue = actionQueue;
    }

    public void addToActionQueue(JobRequest jobRequest) {
        jobRequest.setStatus(JobStatus.Queued);
        this.addToJobQueue(jobRequest);

        // add job to rabbit server if there's availability
        // TODO: needs testing and documentation

        if (nodesAvailable()) {
            // TODO: send to rabbit mq
        } else {
            this.actionQueue.add(jobRequest);
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
        jobRequest.setStatus(JobStatus.Queued);

        if (nodesAvailable()) {
            queNextBlender();
        } else {
            this.blenderJobs.add(jobRequest);
        }
    }

    public void queNextBlender() {
        ArrayList<JobRequest> openBlenderJobs = new ArrayList<>();
        for (JobRequest job : jobQueue) {
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

        for (JobRequest blenderJob : blenderJobs) {
            if (blenderJob.getProjectFile().equals(lowestCount.getProjectFile())) {
                addToActionQueue(blenderJob);
                removeBlenderJob(blenderJob);
            }
        }
    }

    public void removeBlenderJob(JobRequest blenderJob) {
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
