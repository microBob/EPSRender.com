package com.kyang.epsrender.Enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum NodeStatus {
    Ready,
    Rendering,
    Offline
}
