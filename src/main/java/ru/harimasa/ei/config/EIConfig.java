package ru.harimasa.ei.config;

import imgui.type.ImBoolean;

// Седня сделать конфиг
public class EIConfig {
    public int page = 0;
    public boolean textureMode = true;
    public final ImBoolean gradient = new ImBoolean(false);
    public boolean modEnabled = true;
    public final float[] mainColor = { 0.75f, 0.33f, 1.0f };
    public float uiScalePercent = 100.0f;
    public int enchantment = 0;
    public final float[] tint = { 0.67f, 0.33f, 1.0f, 1.0f };
}