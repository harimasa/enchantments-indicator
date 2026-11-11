package ru.harimasa.ei.gui.menu.component;

import imgui.ImGui;
import net.minecraft.client.Minecraft;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public class ComponentHeader {
    public static void render() {
        ImGui.beginChild("header", 0, s(45), false);
        ImGui.setCursorPos(s(18), s(13));
        ImGui.textColored(0.75f, 0.35f, 1.0f, 1.0f, "✧");
        ImGui.sameLine();
        ImGui.setWindowFontScale(1.15f);
        ImGui.textColored(1.0f, 1.0f, 1.0f, 1.0f, "Enchantments Indicator");
        ImGui.setWindowFontScale(1.0f);
        ImGui.setCursorPos(s(570), s(10));
        if (ImGui.button("X", s(25), s(24))) {
            Minecraft.getInstance().setScreen(null);
        }
        ImGui.endChild();
    }
}