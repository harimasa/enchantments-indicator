package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.gui.menu.util.ImGuiUtil;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ComponentSubColors {

    private static final String[] ENCHANTMENTS = {
            "Protection", "Fire Protection", "Blast Protection", "Projectile Protection",
            "Unbreaking", "Mending", "Thorns", "Curse of Binding", "Curse of Vanishing",
            "Respiration", "Aqua Affinity", "Swift Sneak", "Feather Falling", "Depth Strider",
            "Frost Walker", "Soul Speed"
    };

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

        ImGui.setCursorPosX(startX);
        if (modeButton("Tint", !config.textureMode, buttonWidth)) {
            config.textureMode = false;
        }
        ImGui.sameLine(0.0f, buttonGap);
        if (modeButton("Texture", config.textureMode, buttonWidth)) {
            config.textureMode = true;
        }
        ImGuiUtil.rowSeparator(startX);

        ImGuiUtil.formLabel("Enchantment", startX);
        ImGuiUtil.formControlWidth();
        if (ImGui.beginCombo("##enchantments", ENCHANTMENTS[config.enchantment])) {
            for (int i = 0; i < ENCHANTMENTS.length; i++) {
                if (ImGui.selectable(ENCHANTMENTS[i], config.enchantment == i)) {
                    config.enchantment = i;
                }
            }
            ImGui.endCombo();
        }
        ImGuiUtil.rowSeparator(startX);

        ImGuiUtil.formLabel("Enchantment Color", startX);
        ImGuiUtil.formControlWidth();
        if (config.textureMode) {
            float[] rgbTint = {config.tint[0], config.tint[1], config.tint[2]};
            if (ImGui.colorEdit3("##tint", rgbTint)) {
                config.tint[0] = rgbTint[0];
                config.tint[1] = rgbTint[1];
                config.tint[2] = rgbTint[2];
            }
        } else {
            ImGui.colorEdit4("##tint", config.tint);
        }
        ImGuiUtil.rowSeparator(startX);

        if (!config.textureMode) {
            ImGuiUtil.formCheckbox("Gradient", "##gradient", config.gradient.get(), startX, config.gradient::set);
            ImGuiUtil.rowSeparator(startX);
        }
        ImGui.spacing();
        ImGuiUtil.note("Changes are applied immediately while playing.", startX);
    }

    private static boolean modeButton(String text, boolean selected, float width) {
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, s(1.0f));
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0.17f, 0.10f, 0.23f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.22f, 0.12f, 0.30f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.Border, 0.75f, 0.30f, 1.0f, 1.0f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Border, 0.20f, 0.21f, 0.25f, 1.0f);
        }
        boolean clicked = ImGui.button(text, width, s(30.0f));

        if (selected) {
            ImGui.popStyleColor(3);
        } else {
            ImGui.popStyleColor(1);
        }
        ImGui.popStyleVar(1);
        return clicked;
    }
}