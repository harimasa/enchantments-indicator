package ru.harimasa.ei.config.enums;

import com.google.gson.annotations.SerializedName;

public enum RenderMode {
    @SerializedName("texture") TEXTURE,
    @SerializedName(value = "glint", alternate = "tint") GLINT
}