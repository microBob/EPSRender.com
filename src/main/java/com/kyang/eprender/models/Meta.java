package com.kyang.eprender.models;

import java.util.ArrayList;

public class Meta {
    private ArrayList<JobRequest> jobQueue = new ArrayList<>();
    private ArrayList<JobRequest> actionQueue = new ArrayList<>();
    private ArrayList<JobRequest> blenderJobs = new ArrayList<>();

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
}
