package com.kyang.eprender.models;

import com.kyang.eprender.Enums.ProjectType;
import com.kyang.eprender.Enums.JobStatus;

public class JobRequest {
    // SECTION: Properties
    private String useremail;
    private ProjectType projectType;
    private boolean blenderUseAll;
    private int blenderStartFrame;
    private int blenderEndFrame;
    private String projectLocation;
    private JobStatus status;
    private int blenderFramesRendered;
    private int blenderCurrentFrame;
    private int blenderDistributedAmount;


    // SECTION: Constructors
    // shortcut for Adobe or blender that uses all frames
    public JobRequest(String useremail, int projectType, String projectLocation) {
        this.useremail = useremail;
        this.projectType = ProjectType.values()[projectType];
        this.projectLocation = projectLocation;
        status = JobStatus.Unassigned;

        if (projectType > 1) { // using blender type
            this.blenderUseAll = true;

//            ProcessBuilder pb = new ProcessBuilder().command()
        }
    }


    // SECTION: Delegate methods
}
