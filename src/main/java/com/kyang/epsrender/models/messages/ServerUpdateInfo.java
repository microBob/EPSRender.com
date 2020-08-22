package com.kyang.epsrender.models.messages;

import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.models.server.Node;
import com.kyang.epsrender.models.server.NodeSortingComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ServerUpdateInfo {
    // SECTION: Properties
    private ArrayList<JobRequest> jobQueue = new ArrayList<>();
    private ArrayList<Node> serverStat = new ArrayList<>();


    // SECTION: Constructors
    public ServerUpdateInfo(ArrayList<JobRequest> jobQueue, ArrayList<Node> serverStat) {
        this.jobQueue = jobQueue;
        this.serverStat = serverStat;
    }

    public ServerUpdateInfo(ArrayList<JobRequest> jobQueue, ArrayList<JobRequest> verifyingQueue, ArrayList<Node> nodes) {
        ArrayList<JobRequest> jobQueueMerged = new ArrayList<>(verifyingQueue);
        ArrayList<Node> nodesSorted = new ArrayList<>(nodes);

        // merge in verifying -> rendering -> queued
        for (JobRequest jr : jobQueue) {
            if (jr.getJobStatus().equals(JobStatus.Rendering)) {
                jobQueueMerged.add(jr);
            }
        }
        for (JobRequest jr : jobQueue) {
            if (jr.getJobStatus().equals(JobStatus.Queued)) {
                jobQueueMerged.add(jr);
            }
        }

        // sort nodes based on comparator
        nodesSorted.sort(new NodeSortingComparator());

        this.jobQueue = jobQueueMerged;
        this.serverStat = nodesSorted;
    }

    // SECTION: Getters and setters
    public ArrayList<JobRequest> getJobQueue() {
        return jobQueue;
    }

    public void setJobQueue(ArrayList<JobRequest> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public ArrayList<Node> getServerStat() {
        return serverStat;
    }

    public void setServerStat(ArrayList<Node> serverStat) {
        this.serverStat = serverStat;
    }
}
