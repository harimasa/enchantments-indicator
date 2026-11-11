package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import imgui.type.ImBoolean;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.gui.ImGuiRuntime;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public class ComponentSubSettings {
    private static final float[] tempScaleArr = new float[1];

    public static void render(EIConfig config) {
        float startX = ImGui.getCursorPosX();
        float rightColumnX = startX + s(190);
        float elementWidth = s(213);

        ImGui.setCursorPosX(startX);
        ImGui.text("General Settings");
        ImGui.spacing();
        ImGui.separator();

        // Enable Mod
        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();
        ImGui.text("Enable Mod");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);
        ImBoolean modEnabledBool = new ImBoolean(config.modEnabled);
        if (ImGui.checkbox("##modEnabled", modEnabledBool)) config.modEnabled = modEnabledBool.get();

        ImGui.setCursorPosX(startX);
        ImGui.separator();

        // Main Color
        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();
        ImGui.text("Main Mod Color");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);
        ImGui.setNextItemWidth(elementWidth);
        ImGui.colorEdit3("##mainColor", config.mainColor);

        ImGui.setCursorPosX(startX);
        ImGui.separator();

        // UI Scale
        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();
        ImGui.text("UI Scale");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);
        ImGui.setNextItemWidth(elementWidth);
        if (!ImGui.isItemActive() && !ImGui.isItemHovered()) tempScaleArr[0] = config.uiScalePercent;
        if (ImGui.sliderFloat("##uiScale", tempScaleArr, 50.0f, 100.0f, "%.0f%%")) {
            tempScaleArr[0] = Math.clamp(tempScaleArr[0], 50.0f, 100.0f);
            config.uiScalePercent = tempScaleArr[0];
        }
        if (ImGui.isItemDeactivatedAfterEdit()) ImGuiRuntime.UI_SCALE = config.uiScalePercent / 100.0f;

        ImGui.setCursorPosX(startX);
        ImGui.separator();
    }
}