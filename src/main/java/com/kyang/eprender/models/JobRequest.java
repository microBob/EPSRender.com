package com.kyang.eprender.models;

import com.kyang.eprender.Enums.ProjectType;
import com.kyang.eprender.Enums.JobStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JobRequest {
    // SECTION: Properties
    private String useremail;
    private ProjectType projectType;
    private boolean blenderUseAll;
    private int blenderStartFrame;
    private int blenderEndFrame;
    private String projectFile;
    private JobStatus status;
    private int blenderFramesRendered;
    private int blenderCurrentFrame;
    private int blenderDistributedAmount;


    // SECTION: Constructors
    // shortcut for Adobe or blender that uses all frames
    public JobRequest(String useremail, int projectType, String projectLocation) {
        this.useremail = useremail;
        this.projectType = ProjectType.values()[projectType];
        this.projectFile = IdentifyProjectFile(projectLocation, this.projectType);
        status = JobStatus.Unassigned;

        if (projectType > 1) { // using blender type
            this.blenderUseAll = true;

            // extract start and end frames
            try {
                List<String> commands = new ArrayList<>();
//                commands.add("\"/home/microbobu/Applications/blender-2.83.1-linux64/blender\"");
//                commands.add("-b");
//                commands.add("\""+this.projectFile+"\"");
//                commands.add("--python");
//                commands.add("\"/home/microbobu/Documents/EPS Render Server/EPRender.com/src/main/resources/getFrames.py\"");
//                commands.add("|");
//                commands.add("grep");
//                commands.add("-w");
//                commands.add("\"EPRenderInterestedFrames:\"");
                commands.add("/home/microbobu/Documents/EPS Render Server/EPRender.com/src/main/resources/getFrames.sh");
                commands.add("\""+this.projectFile+"\"");

                ProcessBuilder pb = new ProcessBuilder(commands);

//                Runtime rt = Runtime.getRuntime();
                Process proc = pb.start();
//                String command = "/home/microbobu/Applications/blender-2.83.1-linux64/blender -b '" + this.projectFile + "' --python '/home/microbobu/Documents/EPS Render Server/EPRender.com/src/main/resources/getFrames.py'";
//                String command = "/home/microbobu/Applications/blender-2.83.1-linux64/blender -b '" + this.projectFile + "'";
//                Process proc = rt.exec(command);

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                List<String> outputLines = new ArrayList<>();
                String output;
                while ((output = stdInput.readLine()) != null) {
                    outputLines.add(output);
                }
                for (String line : outputLines) {
                    if (line.contains("EPRenderInterestedFrames:")) {
                        String[] frames = line.split("\\D+");
                        blenderStartFrame = Integer.parseInt(frames[0]);
                        blenderEndFrame = Integer.parseInt(frames[1]);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: add abort
            }
        }
    }


    // SECTION: internal methods
    private String IdentifyProjectFile(String projectLocation, ProjectType projectType) {
        File projectDir = new File(projectLocation);
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

//        File[] files = projectDir.listFiles(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                switch (projectType) {
//                    case PremierePro:
//                        return name.endsWith(".prproj");
//                    case AfterEffects:
//                        return name.endsWith(".aep") || name.endsWith(".aepx");
//                    default: // assume using blender
//                        return name.endsWith(".blend");
//                }
//            }
//        });

//        System.out.println("[Debug]: toPath().toString() => " + files[0].toPath().toString());
//        System.out.println("[Debug]: toString() => " + files[0].toString());
//        return files[0].toPath().toString();
    }


    // SECTION: Delegate methods

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
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
