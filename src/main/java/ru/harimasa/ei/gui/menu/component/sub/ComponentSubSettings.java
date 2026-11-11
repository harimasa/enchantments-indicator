package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.GeneralConfig;
import ru.harimasa.ei.gui.ImGuiRuntime;
import ru.harimasa.ei.gui.menu.util.ImGuiUtil;
import ru.harimasa.ei.lang.Lang;

public final class ComponentSubSettings {
    private static final float[] tempScale = new float[1];
    private static boolean scaleInteracting;

    private ComponentSubSettings() {}

    public static void render(EIConfig config) {
        float startX = ImGui.getCursorPosX();

        ImGui.setCursorPosX(startX);
        ImGui.text(Lang.get("ei.settings.title"));
        ImGui.spacing();
        ImGuiUtil.rowSeparator(startX);

        ImGuiUtil.formCheckbox(Lang.get("ei.settings.enable_mod"), "##modEnabled", config.general.modEnabled, startX,
                value -> {
                    config.general.modEnabled = value;
                    ConfigManager.markDirty();
                });
        ImGuiUtil.rowSeparator(startX);
        ImGuiUtil.formLabel(Lang.get("ei.settings.main_color"), startX);
        ImGuiUtil.formControlWidth();
        float[] mainColor = config.general.mainColor.rgb();
        if (ImGui.colorEdit3("##mainColor", mainColor)) {
            config.general.mainColor.setRgb(mainColor);
            ConfigManager.markDirty();
        }
        ImGuiUtil.rowSeparator(startX);
        ImGuiUtil.formLabel(Lang.get("ei.settings.ui_scale"), startX);
        ImGuiUtil.formControlWidth();

        if (!scaleInteracting) {
            tempScale[0] = config.general.uiScale;
        }

        boolean scaleChanged = ImGui.sliderFloat("##uiScale", tempScale, GeneralConfig.MIN_UI_SCALE, GeneralConfig.MAX_UI_SCALE, "%.0f%%");
        scaleInteracting = ImGui.isItemActive() || ImGui.isItemHovered();

        if (scaleChanged) {
            config.general.uiScale = Math.clamp(tempScale[0], GeneralConfig.MIN_UI_SCALE, GeneralConfig.MAX_UI_SCALE);
        }

        if (ImGui.isItemDeactivatedAfterEdit()) {
            ImGuiRuntime.setUiScale(config.general.uiScale / 100.0f);
            ConfigManager.markDirty();
        }
        ImGuiUtil.rowSeparator(startX);

        ImGui.spacing();
        ImGuiUtil.note(Lang.get("ei.settings.note"), startX);
    }
}