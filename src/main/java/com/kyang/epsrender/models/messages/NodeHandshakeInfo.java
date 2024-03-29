package com.kyang.epsrender.models.messages;

import com.kyang.epsrender.Enums.PowerIndex;

public class NodeHandshakeInfo {
    //// SECTION: Properties
    private String ctxSessionID;
    private String nodeName;
    private PowerIndex powerIndex;
    //// SECTION ^: Properties


    //// SECTION: Constructors
    public NodeHandshakeInfo(String ctxSessionID, String nodeName, PowerIndex powerIndex) {
        this.ctxSessionID = ctxSessionID;
        this.nodeName = nodeName;
        this.powerIndex = powerIndex;
    }

    public NodeHandshakeInfo() {
    }
    //// SECTION^: Constructors


    //// SECTION: Getters and setters
    public String getCtxSessionID() {
        return ctxSessionID;
    }

    public void setCtxSessionID(String ctxSessionID) {
        this.ctxSessionID = ctxSessionID;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public PowerIndex getPowerIndex() {
        return powerIndex;
    }

    public void setPowerIndex(PowerIndex powerIndex) {
        this.powerIndex = powerIndex;
    }
    //// SECTION ^: Getters and setters
}
