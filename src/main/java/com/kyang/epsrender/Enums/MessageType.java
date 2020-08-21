package com.kyang.epsrender.Enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum MessageType {
    BlenderFrames,
}
