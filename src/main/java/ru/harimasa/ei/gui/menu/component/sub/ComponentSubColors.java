package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.EnchantmentOption;
import ru.harimasa.ei.config.RenderMode;
import ru.harimasa.ei.gui.menu.util.ImGuiUtil;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ComponentSubColors {
    private ComponentSubColors() {}

    public static void render(EIConfig config) {
        float startX = ImGui.getCursorPosX();

        ImGui.setCursorPosX(startX);
        ImGui.text("Render Mode");
        ImGui.spacing();

        ImGui.setCursorPosX(startX);
        float availableWidth = ImGui.getContentRegionAvail().x;
        float buttonGap = s(8.0f);
        float buttonWidth = Math.max(s(40.0f), (availableWidth - buttonGap) * 0.5f);
        if (ImGuiUtil.modeButton("Tint", config.colors.renderMode == RenderMode.TINT, buttonWidth)) {
            config.colors.renderMode = RenderMode.TINT;
            ConfigManager.markDirty();
        }
        ImGui.sameLine(0.0f, buttonGap);
        if (ImGuiUtil.modeButton("Texture", config.colors.renderMode == RenderMode.TEXTURE, buttonWidth)) {
            config.colors.renderMode = RenderMode.TEXTURE;
            ConfigManager.markDirty();
        }

        boolean tintMode = config.colors.renderMode == RenderMode.TINT;
        ImGuiUtil.rowSeparator(startX);
        EnchantmentOption[] options = EnchantmentOption.values();
        int selected = EnchantmentOption.clampIndex(config.colors.enchantment);
        config.colors.enchantment = selected;

        ImGuiUtil.formLabel("Enchantment", startX);
        ImGuiUtil.formControlWidth();
        if (ImGui.beginCombo("##enchantments", options[selected].label())) {
            for (int i = 0; i < options.length; i++) {
                if (ImGui.selectable(options[i].label(), selected == i)) {
                    config.colors.enchantment = i;
                    ConfigManager.markDirty();
                }
            }
            ImGui.endCombo();
        }

        ImGuiUtil.rowSeparator(startX);

        ImGuiUtil.formLabel("Enchantment Color", startX);
        ImGuiUtil.formControlWidth();
        if (tintMode) {
            float[] rgba = config.colors.enchantmentColor.rgba();
            if (ImGui.colorEdit4("##tint", rgba)) {
                config.colors.enchantmentColor.setRgba(rgba);
                ConfigManager.markDirty();
            }
        } else {
            float[] rgb = config.colors.enchantmentColor.rgb();
            if (ImGui.colorEdit3("##tint", rgb)) {
                config.colors.enchantmentColor.setRgb(rgb);
                ConfigManager.markDirty();
            }
        }
        ImGuiUtil.rowSeparator(startX);

        if (tintMode) {
            ImGuiUtil.formCheckbox("Gradient", "##gradient", config.colors.gradient, startX,
                    value -> {
                        config.colors.gradient = value;
                        ConfigManager.markDirty();
                    });
            ImGuiUtil.rowSeparator(startX);
        }

        ImGui.spacing();
        ImGuiUtil.note("Allows enchantments to be displayed on the player.", startX);
    }
}