package com.kyang.epsrender.models.server;

import com.kyang.epsrender.EPSRenderCore;
import com.kyang.epsrender.Enums.MessageType;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.Enums.PowerIndex;
import com.kyang.epsrender.Enums.ProjectType;
import com.kyang.epsrender.models.messages.JobRequest;
import com.kyang.epsrender.models.messages.Message;

public class Node {
    //// SECTION: Properties
    private String nodeName;
    private String ctxSessionID;
    private PowerIndex powerIndex;
    private NodeStatus nodeStatus = NodeStatus.Ready;
    private JobRequest currentJob;
    //// SECTION ^: Properties


    //// SECTION: Constructors
    public Node(String nodeName, String ctxSessionID, PowerIndex powerIndex) {
        this.nodeName = nodeName;
        this.ctxSessionID = ctxSessionID;
        this.powerIndex = powerIndex;
    }

    public Node(String ctxSessionID) {
        this.ctxSessionID = ctxSessionID;
    }
    //// SECTION ^: Constructors


    //// SECTION: Internal functions
    public Message getNextJob() {
        Meta serverMeta = EPSRenderCore.getServerMeta();

        // Check Verify queue
        JobRequest jobFromVerifyingQueue = serverMeta.getJobFromVerifyingQueue();
        if (jobFromVerifyingQueue != null) {
            System.out.println("[Node " + nodeName + "]:\tVerifying job " + jobFromVerifyingQueue.getProjectFolderName());

            // Set this as current job and set rendering
            this.currentJob = jobFromVerifyingQueue;
            this.nodeStatus = NodeStatus.Rendering;

            ProjectType projectType = jobFromVerifyingQueue.getProjectType();
            return new Message(projectType.equals(ProjectType.PremierePro) ? MessageType.VerifyPremiere :
                    projectType.equals(ProjectType.AfterEffects) ? MessageType.VerifyAE : MessageType.VerifyBlender,
                    jobFromVerifyingQueue);
        }

        // Check Job queue
        JobRequest jobFromJobQueue = serverMeta.getJobFromJobQueue();
        if (jobFromJobQueue != null) {
            System.out.println("[Node " + nodeName + "]:\tRendering job " + jobFromJobQueue.getProjectFolderName());

            // Set this as current job and set rendering
            this.currentJob = jobFromJobQueue;
            this.nodeStatus = NodeStatus.Rendering;

            ProjectType projectType = jobFromJobQueue.getProjectType();
            MessageType messageType;
            if (projectType.equals(ProjectType.BlenderCycles) || projectType.equals(ProjectType.BlenderEEVEE)) {
                messageType = MessageType.RenderBlender;
            } else {
                messageType = MessageType.RenderME;
            }
            return new Message(messageType, jobFromJobQueue);
        }

        // No next job
        return null;
    }
    //// SECTION ^: Internal functions


    //// SECTION: Getters and setters
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getCtxSessionID() {
        return ctxSessionID;
    }

    public void setCtxSessionID(String ctxSessionID) {
        this.ctxSessionID = ctxSessionID;
    }

    public PowerIndex getPowerIndex() {
        return powerIndex;
    }

    public void setPowerIndex(PowerIndex powerIndex) {
        this.powerIndex = powerIndex;
    }

    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public JobRequest getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(JobRequest currentJob) {
        this.currentJob = currentJob;
    }
    //// SECTION ^: Getters and setters
}
