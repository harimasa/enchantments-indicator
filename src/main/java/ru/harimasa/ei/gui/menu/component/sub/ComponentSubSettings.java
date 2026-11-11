package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.gui.ImGuiRuntime;
import ru.harimasa.ei.gui.menu.util.ImGuiUtil;

public final class ComponentSubSettings {

    private static final float MIN_SCALE_PERCENT = 50.0f;
    private static final float MAX_SCALE_PERCENT = 100.0f;

    private static final float[] tempScale = new float[1];
    private static boolean scaleInteracting;

    private ComponentSubSettings() {}

    public static void render(EIConfig config) {
        float startX = ImGui.getCursorPosX();

        ImGui.setCursorPosX(startX);
        ImGui.text("General Settings");
        ImGui.spacing();
        ImGuiUtil.rowSeparator(startX);

        ImGuiUtil.formCheckbox("Enable Mod", "##modEnabled", config.modEnabled, startX, value -> config.modEnabled = value);
        ImGuiUtil.rowSeparator(startX);

        ImGuiUtil.formLabel("Main Mod Color", startX);
        ImGuiUtil.formControlWidth();
        ImGui.colorEdit3("##mainColor", config.mainColor);
        ImGuiUtil.rowSeparator(startX);

        ImGuiUtil.formLabel("UI Scale", startX);
        ImGuiUtil.formControlWidth();

        if (!scaleInteracting) {
            tempScale[0] = config.uiScalePercent;
        }

        boolean scaleChanged = ImGui.sliderFloat("##uiScale", tempScale, MIN_SCALE_PERCENT, MAX_SCALE_PERCENT, "%.0f%%");
        scaleInteracting = ImGui.isItemActive() || ImGui.isItemHovered();
        if (scaleChanged) {
            config.uiScalePercent = Math.clamp(tempScale[0], MIN_SCALE_PERCENT, MAX_SCALE_PERCENT);
        }

        if (ImGui.isItemDeactivatedAfterEdit()) {
            ImGuiRuntime.setUiScale(config.uiScalePercent / 100.0f);
        }
        ImGuiUtil.rowSeparator(startX);
        ImGui.spacing();
        ImGuiUtil.note("UI scale changes the size of the whole menu. " + "It is applied when you release the slider.", startX);
    }
}