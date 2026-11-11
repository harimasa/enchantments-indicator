package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;

public class ComponentSubInventory {
    public static void render() {
        ImGui.text("Inventory Visuals");
        ImGui.spacing();
        ImGui.separator();
        ImGui.textDisabled("Inventory highlight settings will be added here.");
    }
}