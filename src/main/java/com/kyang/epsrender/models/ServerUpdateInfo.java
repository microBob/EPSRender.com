package com.kyang.epsrender.models;

import java.util.ArrayList;

public class ServerUpdateInfo {
    // SECTION: Properties
    private ArrayList<JobRequest> jobQueue = new ArrayList<>();
    private ArrayList<Node> serverStat = new ArrayList<>();


    // SECTION: Constructors
    public ServerUpdateInfo(ArrayList<JobRequest> jobQueue, ArrayList<Node> serverStat) {
        this.jobQueue = jobQueue;
        this.serverStat = serverStat;
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
