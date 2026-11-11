package ru.harimasa.ei.gui.menu.util;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public class ImGuiTheme {
    public static final int THEME_COLOR_COUNT = 23;

    public static void pushTheme() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, s(12));
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, s(1));
        ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, s(6));
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, s(4));
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, s(2), s(2));
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, s(10), s(7));
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, s(8), s(8));

        ImGui.pushStyleColor(ImGuiCol.Text, 0.91f, 0.91f, 0.95f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.TextDisabled, 0.53f, 0.54f, 0.61f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.045f, 0.047f, 0.058f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.045f, 0.047f, 0.058f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.PopupBg, 0.075f, 0.077f, 0.095f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.20f, 0.21f, 0.25f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.085f, 0.09f, 0.115f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.14f, 0.13f, 0.18f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0.18f, 0.14f, 0.23f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.TitleBg, 0.045f, 0.047f, 0.058f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.TitleBgActive, 0.045f, 0.047f, 0.058f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.085f, 0.09f, 0.115f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.17f, 0.15f, 0.22f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.23f, 0.16f, 0.29f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.Header, 0.11f, 0.12f, 0.16f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0.17f, 0.15f, 0.22f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0.23f, 0.13f, 0.30f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.CheckMark, 0.75f, 0.33f, 1.0f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.SliderGrab, 0.70f, 0.30f, 0.95f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.SliderGrabActive, 0.85f, 0.45f, 1.0f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.Separator, 0.19f, 0.20f, 0.24f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.SeparatorHovered, 0.65f, 0.30f, 0.90f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.SeparatorActive, 0.75f, 0.33f, 1.0f, 1.0f);
    }
}