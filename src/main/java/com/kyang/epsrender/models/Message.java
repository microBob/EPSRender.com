package com.kyang.epsrender.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kyang.epsrender.Enums.MessageType;

public class Message {
    private MessageType type;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = BlenderFrames.class, name = "BlenderFrames")
    })
    private MessageData data;

    public Message(MessageType type, MessageData data) {
        this.type = type;
        this.data = data;
    }

    public Message() {
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(MessageData data) {
        this.data = data;
    }
}
