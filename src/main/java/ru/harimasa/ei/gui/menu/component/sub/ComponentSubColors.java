package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.enums.EnchantmentOption;
import ru.harimasa.ei.config.enums.RenderMode;
import ru.harimasa.ei.gui.menu.util.ImGuiUtil;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ComponentSubColors {
    private static final float[] tempIntensity = new float[1];
    private static boolean intensityInteracting;

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
        if (ImGuiUtil.modeButton("Glint", config.colors.renderMode == RenderMode.GLINT, buttonWidth)) {
            config.colors.renderMode = RenderMode.GLINT;
            ConfigManager.markDirty();
        }
        ImGui.sameLine(0.0f, buttonGap);
        if (ImGuiUtil.modeButton("Texture", config.colors.renderMode == RenderMode.TEXTURE, buttonWidth)) {
            config.colors.renderMode = RenderMode.TEXTURE;
            ConfigManager.markDirty();
        }

        boolean glintMode = config.colors.renderMode == RenderMode.GLINT;
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

        if (glintMode) {
            ImGuiUtil.formCheckbox("Gradient", "##gradient", config.colors.gradient, startX,
                    value -> {
                        config.colors.gradient = value;
                        ConfigManager.markDirty();
                    });
            ImGuiUtil.rowSeparator(startX);
        }

        ImGuiUtil.formLabel("Enchantment Color", startX);
        if (glintMode && config.colors.gradient) {
            gradientColors(config);
        } else {
            ImGuiUtil.formControlWidth();
            float[] rgb = config.colors.enchantmentColor.rgb();
            if (ImGui.colorEdit3("##glint", rgb)) {
                config.colors.enchantmentColor.setRgb(rgb);
                ConfigManager.markDirty();
            }
        }
        ImGuiUtil.rowSeparator(startX);

        if (glintMode) {
            ImGuiUtil.formLabel("Intensity", startX);
            ImGuiUtil.formControlWidth();
            if (!intensityInteracting) {
                tempIntensity[0] = config.colors.intensity * 100.0f;
            }
            boolean intensityChanged = ImGui.sliderFloat("##glintIntensity", tempIntensity, 0.0f, 200.0f, "%.0f%%");
            intensityInteracting = ImGui.isItemActive() || ImGui.isItemHovered();

            if (intensityChanged) {
                config.colors.intensity = Math.clamp(tempIntensity[0] / 100.0f, 0.0f, 2.0f);
                ConfigManager.markDirty();
            }
            ImGuiUtil.rowSeparator(startX);
        }
        ImGui.spacing();
        ImGuiUtil.note(glintMode ? "Glint replaces the enchantment glint. Gradient blends From (top) into To (bottom)." + " Intensity scales the glow." : "Texture recolors the armor itself when the selected enchantment is present.", startX);
    }

    private static void gradientColors(EIConfig config) {
        float gap = s(6.0f);

        ImGui.alignTextToFramePadding();
        ImGui.text("From");
        ImGui.sameLine(0.0f, gap);

        float[] from = config.colors.gradientFrom.rgb();
        if (ImGui.colorEdit3("##gradientFrom", from, ImGuiColorEditFlags.NoInputs)) {
            config.colors.gradientFrom.setRgb(from);
            ConfigManager.markDirty();
        }
        ImGui.setItemTooltip("From (top)");

        ImGui.sameLine(0.0f, gap);
        ImGui.alignTextToFramePadding();
        ImGui.text("->");
        ImGui.sameLine(0.0f, gap);

        ImGui.alignTextToFramePadding();
        ImGui.text("To");
        ImGui.sameLine(0.0f, gap);

        float[] to = config.colors.gradientTo.rgb();
        if (ImGui.colorEdit3("##gradientTo", to, ImGuiColorEditFlags.NoInputs)) {
            config.colors.gradientTo.setRgb(to);
            ConfigManager.markDirty();
        }
        ImGui.setItemTooltip("To (bottom)");
    }
}