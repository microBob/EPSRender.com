package com.kyang.epsrender.models;

import com.kyang.epsrender.Enums.ProjectType;
import com.kyang.epsrender.Enums.JobStatus;

import java.io.*;

public class JobRequest {
    // SECTION: Properties
    private String userEmail;
    private ProjectType projectType;
    private boolean blenderUseAll = false;
    private int blenderStartFrame;
    private int blenderEndFrame;
    private String projectFile;
    private JobStatus status;
    private int blenderFramesRendered = 0;
    private int blenderCurrentFrame;
    private int blenderDistributedAmount;


    // SECTION: Constructors
    // shortcut for Adobe or blender that uses all frames
    public JobRequest(String userEmail, int projectType, String projectFolderName) {
        this.userEmail = userEmail;
        this.projectType = ProjectType.values()[projectType];
        this.projectFile = IdentifyProjectFile(projectFolderName, this.projectType);

        if (projectType > 1) { // using blender type
            blenderUseAll = true;
        }
    }

    // for blender with specific frames
    public JobRequest(String userEmail, int projectType, String projectFolderName, int blenderStartFrame, int blenderEndFrame) {
        this.userEmail = userEmail;
        this.projectType = ProjectType.values()[projectType];
        this.projectFile = IdentifyProjectFile(projectFolderName, this.projectType);
        this.blenderStartFrame = blenderStartFrame;
        this.blenderEndFrame = blenderEndFrame;
    }


    // SECTION: internal methods
    private String IdentifyProjectFile(String projectFolderName, ProjectType projectType) {
        File projectDir = new File(projectFolderName);
        File[] files = projectDir.listFiles();

        if (files == null) {
            // Abort!
            System.out.println("[Reject Error]: project directory invalid!");
            // TODO: create/add reject function
            return "";
        } else if (files.length == 0) {
            // Abort!
            System.out.println("[Reject Error]: project directory invalid!");
            // TODO: create/add reject function
            return "";
        } else {
            File targetFile = null;
            boolean failed = false;
            fileSearch:
            for (File f : files) {
                switch (projectType) {
                    case PremierePro:
                        if (f.toString().endsWith(".prproj")) {
                            if (targetFile == null) {
                                targetFile = f;
                            } else {
                                failed = true;
                                break fileSearch;
                            }
                        }
                        break;
                    case AfterEffects:
                        if (f.toString().endsWith(".aep") || f.toString().endsWith(".aepx")) {
                            if (targetFile == null) {
                                targetFile = f;
                            } else {
                                failed = true;
                                break fileSearch;
                            }
                        }
                        break;
                    default:
                        if (f.toString().endsWith(".blend")) {
                            if (targetFile == null) {
                                targetFile = f;
                            } else {
                                failed = true;
                                break fileSearch;
                            }
                        }
                        break;
                }
            }

            if (failed) {
                // Abort!
                System.out.println("[Reject Error]: multiple project files detected!");
                // TODO: create/add reject function
                return "";
            } else if (targetFile == null) {
                // Abort!
                System.out.println("[Reject Error]: could not find project file in directory!");
                // TODO: create/add reject function
                return "";
            } else {
                return targetFile.toString();
            }
        }
    }


    // SECTION: Delegate methods

    public String getUserEmail() {
        return userEmail;
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

    public boolean isBlenderUseAll() {
        return blenderUseAll;
    }

    public void setBlenderUseAll(boolean blenderUseAll) {
        this.blenderUseAll = blenderUseAll;
    }

    public int getBlenderStartFrame() {
        return blenderStartFrame;
    }

    public void setBlenderStartFrame(int blenderStartFrame) {
        this.blenderStartFrame = blenderStartFrame;
    }

    public int getBlenderEndFrame() {
        return blenderEndFrame;
    }

    public void setBlenderEndFrame(int blenderEndFrame) {
        this.blenderEndFrame = blenderEndFrame;
    }

    public String getProjectFile() {
        return projectFile;
    }

    public void setProjectFile(String projectFile) {
        this.projectFile = projectFile;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public int getBlenderFramesRendered() {
        return blenderFramesRendered;
    }

    public void setBlenderFramesRendered(int blenderFramesRendered) {
        this.blenderFramesRendered = blenderFramesRendered;
    }

    public int getBlenderCurrentFrame() {
        return blenderCurrentFrame;
    }

    public void setBlenderCurrentFrame(int blenderCurrentFrame) {
        this.blenderCurrentFrame = blenderCurrentFrame;
    }

    public int getBlenderDistributedAmount() {
        return blenderDistributedAmount;
    }

    public void setBlenderDistributedAmount(int blenderDistributedAmount) {
        this.blenderDistributedAmount = blenderDistributedAmount;
    }
}
