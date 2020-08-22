package com.kyang.epsrender.Enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum ProjectType {
    PremierePro,
    AfterEffects,
    BlenderCycles,
    BlenderEEVEE
}
