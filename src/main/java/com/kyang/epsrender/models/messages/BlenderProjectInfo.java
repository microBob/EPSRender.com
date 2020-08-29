package com.kyang.epsrender.models.messages;

public class BlenderProjectInfo {
    // SECTION: Properties
    private int startFrame;
    private int endFrame;
    private boolean useAllFrames;
    private String fileName;
    private int frameNumber;
    private int framesCompleted;
    private int renderers;


    // SECTION: Constructors
    public BlenderProjectInfo(int start, int end) {
        this.startFrame = start;
        this.endFrame = end;
    }

    public BlenderProjectInfo(boolean useAllFrames) {
        this.useAllFrames = useAllFrames;
    }

    public BlenderProjectInfo() {
    }


    // SECTION: Getters and setters
    public int getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

    public boolean getUseAllFrames() {
        return useAllFrames;
    }

    public void setUseAllFrames(boolean useAllFrames) {
        this.useAllFrames = useAllFrames;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public int getFramesCompleted() {
        return framesCompleted;
    }

    public void setFramesCompleted(int framesCompleted) {
        this.framesCompleted = framesCompleted;
    }

    public int getRenderers() {
        return renderers;
    }

    public void setRenderers(int renderers) {
        this.renderers = renderers;
    }
}
