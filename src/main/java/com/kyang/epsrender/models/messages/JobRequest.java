package com.kyang.epsrender.models.messages;

import com.kyang.epsrender.Enums.ProjectType;
import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.models.messages.BlenderProjectInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JobRequest {
    // SECTION: Properties
    private String userEmail;
    private ProjectType projectType;
    private String projectFolderName;
    private BlenderProjectInfo blenderInfo;

    private boolean verified;
    private String errorMsg;
    private String timeAdded;
    private JobStatus jobStatus = JobStatus.Verifying;


    // SECTION: Constructors
    public JobRequest(String userEmail, ProjectType projectType, String projectFolderName, BlenderProjectInfo blenderInfo) {
        this.userEmail = userEmail;
        this.projectType = projectType;
        this.projectFolderName = projectFolderName;
        this.blenderInfo = blenderInfo;

        setTimeAddedNow();
    }


    // SECTION: Getters and setters
    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        int atIndex = userEmail.indexOf("@");
        if (atIndex > 0) {
            return userEmail.substring(0, atIndex);
        } else {
            return userEmail;
        }
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public String getProjectFolderName() {
        return projectFolderName;
    }

    public void setProjectFolderName(String projectFolderName) {
        this.projectFolderName = projectFolderName;
    }

    public BlenderProjectInfo getBlenderInfo() {
        return blenderInfo;
    }

    public void setBlenderInfo(BlenderProjectInfo blenderInfo) {
        this.blenderInfo = blenderInfo;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(String timeAdded) {
        this.timeAdded = timeAdded;
    }

    private void setTimeAddedNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        this.timeAdded = formatter.format(new Date());
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }
}
