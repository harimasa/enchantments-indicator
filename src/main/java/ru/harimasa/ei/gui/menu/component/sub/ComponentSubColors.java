package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import ru.harimasa.ei.config.EIConfig;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public class ComponentSubColors {
    public static void render(EIConfig config) {
        float startX = ImGui.getCursorPosX();
        ImGui.setCursorPosX(startX);
        ImGui.text("Render Mode");
        ImGui.spacing();

        float rightColumnX = startX + s(190);
        float elementWidth = s(213);
        float rightEdge = rightColumnX + elementWidth;
        float buttonSpacing = s(8);
        float buttonWidth = (rightEdge - startX - buttonSpacing) / 2.0f;

        ImGui.setCursorPosX(startX);
        if (modeButton("Tint", !config.textureMode, buttonWidth)) config.textureMode = false;
        ImGui.sameLine(0, buttonSpacing);
        if (modeButton("Texture", config.textureMode, buttonWidth)) config.textureMode = true;
        ImGui.spacing();

        ImGui.setCursorPosX(startX);
        ImGui.separator();

        // Enchantment Dropdown
        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();
        ImGui.text("Enchantment");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);

        String[] modes = { "Protection", "Fire Protection", "Blast Protection", "Projectile Protection",
                "Unbreaking", "Mending", "Thorns", "Curse of Binding", "Curse of Vanishing",
                "Respiration", "Aqua Affinity", "Swift Sneak", "Feather Falling", "Depth Strider",
                "Frost Walker", "Soul Speed" };
        ImGui.setNextItemWidth(elementWidth);
        if (ImGui.beginCombo("##enchantments", modes[config.enchantment])) {
            for (int i = 0; i < modes.length; i++) {
                if (ImGui.selectable(modes[i], config.enchantment == i)) config.enchantment = i;
            }
            ImGui.endCombo();
        }

        ImGui.setCursorPosX(startX);
        ImGui.separator();

        // Enchantment Color
        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();
        ImGui.text("Enchantment Color");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);
        ImGui.setNextItemWidth(elementWidth);

        if (config.textureMode) {
            float[] rgbTint = new float[] { config.tint[0], config.tint[1], config.tint[2] };
            if (ImGui.colorEdit3("##tint", rgbTint)) {
                config.tint[0] = rgbTint[0]; config.tint[1] = rgbTint[1]; config.tint[2] = rgbTint[2];
            }
        } else {
            ImGui.colorEdit4("##tint", config.tint);
        }

        ImGui.setCursorPosX(startX);
        ImGui.separator();

        // Gradient Checkbox
        if (!config.textureMode) {
            ImGui.setCursorPosX(startX);
            ImGui.setWindowFontScale(0.95f);
            ImGui.alignTextToFramePadding();
            ImGui.text("Gradient");
            ImGui.setWindowFontScale(1.0f);
            float checkboxSize = ImGui.getFrameHeight();
            ImGui.sameLine(rightEdge - checkboxSize);
            ImGui.checkbox("##gradient", config.gradient);
            ImGui.setCursorPosX(startX);
            ImGui.separator();
        }

        ImGui.spacing();
        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.85f);
        ImGui.textDisabled("Changes are applied immediately while playing.");
        ImGui.setWindowFontScale(1.0f);
    }

    private static boolean modeButton(String text, boolean selected, float width) {
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, s(1));
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0.17f, 0.10f, 0.23f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.22f, 0.12f, 0.30f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.Border, 0.75f, 0.30f, 1.0f, 1.0f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Border, 0.20f, 0.21f, 0.25f, 1.0f);
        }
        boolean clicked = ImGui.button(text, width, s(30));
        if (selected) ImGui.popStyleColor(3); else ImGui.popStyleColor(1);
        ImGui.popStyleVar(1);
        return clicked;
    }
}