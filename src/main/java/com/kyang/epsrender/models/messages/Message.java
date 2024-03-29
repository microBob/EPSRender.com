package com.kyang.epsrender.models.messages;

import com.kyang.epsrender.Enums.MessageType;

public class Message {
    private MessageType type;
    private JobRequest data;

    public Message(MessageType type, JobRequest data) {
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

    public JobRequest getData() {
        return data;
    }

    public void setData(JobRequest data) {
        this.data = data;
    }
}
