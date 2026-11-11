package ru.harimasa.ei.gui.menu.util;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import ru.harimasa.ei.config.ColorConfig;
import ru.harimasa.ei.config.ConfigManager;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public class ImGuiTheme {
    public static final int THEME_COLOR_COUNT = 27;

    private static final float ACCENT_SATURATION = 1.5f;

    private static final float[] WINDOW_BG = {0.045f, 0.047f, 0.058f};
    private static final float[] POPUP_BG = {0.075f, 0.077f, 0.095f};
    private static final float[] BORDER = {0.20f, 0.21f, 0.25f};
    private static final float[] FRAME_BG = {0.085f, 0.09f, 0.115f};
    private static final float[] FRAME_BG_HOVERED = {0.125f, 0.13f, 0.16f};
    private static final float[] FRAME_BG_ACTIVE = {0.16f, 0.165f, 0.20f};
    private static final float[] BUTTON_HOVERED = {0.15f, 0.155f, 0.19f};
    private static final float[] BUTTON_ACTIVE = {0.19f, 0.195f, 0.235f};
    private static final float[] HEADER = {0.11f, 0.12f, 0.16f};
    private static final float[] HEADER_HOVERED = {0.15f, 0.155f, 0.19f};
    private static final float[] HEADER_ACTIVE = {0.19f, 0.195f, 0.235f};
    private static final float[] SEPARATOR = {0.19f, 0.20f, 0.24f};
    private static final float[] PANEL_LIGHT = {0.30f, 0.31f, 0.36f};

    private static final float[] ACCENT = {0.75f, 0.33f, 1.0f};
    private static final float[] SCRATCH = new float[3];

    public static void pushTheme() {
        float[] accent = accent();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, s(12));
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, s(1));
        ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, s(6));
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, s(4));
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, s(2), s(2));
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, s(10), s(7));
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, s(8), s(8));

        ImGui.pushStyleColor(ImGuiCol.Text, 0.91f, 0.91f, 0.95f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.TextDisabled, 0.53f, 0.54f, 0.61f, 1.0f);

        pushColor(ImGuiCol.WindowBg, WINDOW_BG, 0.0f);
        pushColor(ImGuiCol.ChildBg, WINDOW_BG, 0.0f);
        pushColor(ImGuiCol.PopupBg, POPUP_BG);
        pushColor(ImGuiCol.Border, BORDER);
        pushColor(ImGuiCol.FrameBg, FRAME_BG);
        pushColor(ImGuiCol.FrameBgHovered, FRAME_BG_HOVERED);
        pushColor(ImGuiCol.FrameBgActive, FRAME_BG_ACTIVE);
        pushColor(ImGuiCol.TitleBg, WINDOW_BG);
        pushColor(ImGuiCol.TitleBgActive, WINDOW_BG);
        pushColor(ImGuiCol.Button, FRAME_BG);
        pushColor(ImGuiCol.ButtonHovered, BUTTON_HOVERED);
        pushColor(ImGuiCol.ButtonActive, BUTTON_ACTIVE);
        pushColor(ImGuiCol.Header, HEADER);
        pushColor(ImGuiCol.HeaderHovered, HEADER_HOVERED);
        pushColor(ImGuiCol.HeaderActive, HEADER_ACTIVE);
        pushColor(ImGuiCol.CheckMark, accent);
        pushColor(ImGuiCol.SliderGrab, scale(accent, 0.92f));
        pushColor(ImGuiCol.SliderGrabActive, lighten(accent, 0.25f));
        pushColor(ImGuiCol.Separator, SEPARATOR);
        pushColor(ImGuiCol.SeparatorHovered, scale(accent, 0.90f));
        pushColor(ImGuiCol.SeparatorActive, accent);

        pushColor(ImGuiCol.ScrollbarBg, WINDOW_BG, 0.0f);
        pushColor(ImGuiCol.ScrollbarGrab, scale(accent, 0.80f), 0.90f);
        pushColor(ImGuiCol.ScrollbarGrabHovered, scale(accent, 0.90f));
        pushColor(ImGuiCol.ScrollbarGrabActive, accent);
    }

    public static int windowBgColor() {
        return colorU32(WINDOW_BG, 1.0f);
    }

    public static float[] accentColor() {
        float[] accent = accent();
        return new float[]{accent[0], accent[1], accent[2]};
    }

    public static int accentColorU32() {
        return colorU32(accent(), 1.0f);
    }

    public static int accentColorU32(float alpha) {
        return colorU32(accent(), alpha);
    }

    public static float[] borderColor() {
        return new float[]{BORDER[0], BORDER[1], BORDER[2]};
    }

    public static int panelColorU32(float brightness) {
        return colorU32(mix(WINDOW_BG, PANEL_LIGHT, brightness), 1.0f);
    }

    public static float[] panelColor(float brightness) {
        return mix(WINDOW_BG, PANEL_LIGHT, brightness);
    }

    public static int colorU32(float[] color, float alpha) {
        return ImGui.getColorU32(color[0], color[1], color[2], alpha);
    }

    private static float[] accent() {
        ColorConfig main = ConfigManager.get().general.mainColor;
        float r = clamp01(main.r);
        float g = clamp01(main.g);
        float b = clamp01(main.b);

        float max = Math.max(r, Math.max(g, b));
        if (max < 0.02f) {
            ACCENT[0] = 0.72f;
            ACCENT[1] = 0.72f;
            ACCENT[2] = 0.78f;
            return ACCENT;
        }

        float nr = r / max;
        float ng = g / max;
        float nb = b / max;
        float mean = (nr + ng + nb) / 3.0f;

        ACCENT[0] = clamp01(mean + (nr - mean) * ACCENT_SATURATION);
        ACCENT[1] = clamp01(mean + (ng - mean) * ACCENT_SATURATION);
        ACCENT[2] = clamp01(mean + (nb - mean) * ACCENT_SATURATION);
        return ACCENT;
    }

    private static float[] mix(float[] from, float[] to, float amount) {
        float t = clamp01(amount);
        SCRATCH[0] = from[0] + (to[0] - from[0]) * t;
        SCRATCH[1] = from[1] + (to[1] - from[1]) * t;
        SCRATCH[2] = from[2] + (to[2] - from[2]) * t;
        return SCRATCH;
    }

    private static float[] scale(float[] color, float factor) {
        SCRATCH[0] = clamp01(color[0] * factor);
        SCRATCH[1] = clamp01(color[1] * factor);
        SCRATCH[2] = clamp01(color[2] * factor);
        return SCRATCH;
    }

    private static float[] lighten(float[] color, float amount) {
        float t = clamp01(amount);
        SCRATCH[0] = color[0] + (1.0f - color[0]) * t;
        SCRATCH[1] = color[1] + (1.0f - color[1]) * t;
        SCRATCH[2] = color[2] + (1.0f - color[2]) * t;
        return SCRATCH;
    }

    private static void pushColor(int target, float[] color) {
        pushColor(target, color, 1.0f);
    }

    private static void pushColor(int target, float[] color, float alpha) {
        ImGui.pushStyleColor(target, color[0], color[1], color[2], alpha);
    }

    private static float clamp01(float value) {
        return Math.clamp(value, 0.0f, 1.0f);
    }
}