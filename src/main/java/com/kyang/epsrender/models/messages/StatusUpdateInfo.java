package com.kyang.epsrender.models.messages;

import com.kyang.epsrender.EPSRenderCore;
import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.models.server.Node;

import java.util.ArrayList;

public class StatusUpdateInfo {
    //// SECTION: Properties
    private ArrayList<String> jobQueue = new ArrayList<>();
    private ArrayList<String> serverStat = new ArrayList<>();
    //// SECTION ^: Properties


    //// SECTION: Constructors
    public StatusUpdateInfo(ArrayList<JobRequest> jobQueue, ArrayList<JobRequest> verifyingQueue,
                            ArrayList<Node> nodes) {
        String rowClosing = "</td></tr>";

        /// Merge in verifying -> rendering -> queued
        // Merge in verifying
        for (JobRequest vq : verifyingQueue) {
            String row = jobQueueRowStarter(vq, JobStatus.Verifying);
            row += "Verifying";
            row += rowClosing;

            this.jobQueue.add(row);
        }
        // Merge in rendering
        for (JobRequest jr : EPSRenderCore.getServerMeta().getRenderingJobs()) {
            String row = jobQueueRowStarter(jr, JobStatus.Rendering);
            System.out.println("===Job is rendering===");
            // Change display type if blender
            if (jr.getBlenderInfo() != null) {
                System.out.println("Blender rendering");
                row += jr.getBlenderInfo().getFramesCompleted();
                row += " / ";
                row += (jr.getBlenderInfo().getEndFrame() - jr.getBlenderInfo().getStartFrame()) + 1;
                row += " Rendered";
            } else {
                row += "Rendering";
            }
            row += rowClosing;
            this.jobQueue.add(row);
        }
        // Merge in queued
        int placeInQueue = 1;
        for (JobRequest jr : EPSRenderCore.getServerMeta().getQueuedJobs()) {
            System.out.println("===Job is queued===");
            String row = jobQueueRowStarter(jr, JobStatus.Queued);
            row += "Place <strong>" + placeInQueue + "</strong> in Queue";
            row += rowClosing;

            placeInQueue++;
            this.jobQueue.add(row);
        }

        for (Node n : nodes) {
            String row = "<tr><td>";
            row += n.getNodeName();
            row += "</td><td class=\"";
            switch (n.getNodeStatus()) {
                case Ready:
                    row += "text-success";
                    break;
                case Rendering:
                    row += "text-warning";
                    break;
                case Offline:
                    row += "text-danger";
                    break;
                default:
                    System.out.println("[StatusUpdateInfo]:\tUnhandled NodeStatus " + n.getNodeStatus());
                    break;
            }
            row += "\">";
            row += n.getNodeStatus();
            row += rowClosing;

            this.serverStat.add(row);
        }
    }
    //// SECTION ^: Constructors


    //// SECTION: Getters and setters
    private String jobQueueRowStarter(JobRequest jobRequest, JobStatus jobStatus) {
        String rowOpening = "<tr><td>";
        String newCell = "</td><td>";
        String row = rowOpening;
        row += jobRequest.getUserName();
        row += newCell;
        row += jobRequest.getProjectType().toString();
        row += newCell;
        row += jobRequest.getTimeAdded();
        row += "</td><td class=\"";
        switch (jobStatus) {
            case Verifying:
                row += "text-warning";
                break;
            case Queued:
                row += "text-info";
                break;
            case Rendering:
                row += "text-primary";
                break;
            default:
                System.out.println("[StatusUpdateInfo]:\tUnhandled JobStatus " + jobStatus);
                break;
        }
        row += "\">";

        return row;
    }

    public ArrayList<String> getJobQueue() {
        return jobQueue;
    }

    public void setJobQueue(ArrayList<String> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public ArrayList<String> getServerStat() {
        return serverStat;
    }

    public void setServerStat(ArrayList<String> serverStat) {
        this.serverStat = serverStat;
    }
    //// SECTION ^: Getters and setters
}
