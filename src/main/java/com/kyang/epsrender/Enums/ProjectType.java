package com.kyang.epsrender.Enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum ProjectType {
    PremierePro,
    AfterEffects,
    BlenderCycles,
    BlenderEEVEE;

    @Override
    public String toString() {
        switch (this) {
            case PremierePro:
                return "Adobe PremierePro";
            case AfterEffects:
                return "Adobe After Effects";
            case BlenderCycles:
                return "Blender Cycles";
            case BlenderEEVEE:
                return "Blender EEVEE";
            default:
                return super.toString();
        }
    }
}
