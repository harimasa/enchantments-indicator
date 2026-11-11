package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import imgui.type.ImBoolean;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.gui.ImGuiRuntime;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public class ComponentSubSettings {
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
        float[] scaleArr = {config.uiScalePercent};
        if (ImGui.sliderFloat("##uiScale", scaleArr, 50.0f, 100.0f, "%.0f%%")) {
            config.uiScalePercent = scaleArr[0];
            ImGuiRuntime.UI_SCALE = config.uiScalePercent / 100.0f;
        }

        ImGui.setCursorPosX(startX);
        ImGui.separator();
    }
}