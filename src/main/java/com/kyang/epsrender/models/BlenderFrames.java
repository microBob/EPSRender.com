package com.kyang.epsrender.models;

public class BlenderFrames extends MessageData {
    private int startFrame;
    private int endFrame;

    public BlenderFrames(int start, int end) {
        startFrame = start;
        endFrame = end;
    }

    public BlenderFrames() {
    }

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
}
