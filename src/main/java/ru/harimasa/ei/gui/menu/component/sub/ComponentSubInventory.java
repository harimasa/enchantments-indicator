package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;

// Сначало основное потом эту заглушку доделать
public final class ComponentSubInventory {

    private ComponentSubInventory() {}

    public static void render() {
        float startX = ImGui.getCursorPosX();

        ImGui.setCursorPosX(startX);
        ImGui.text("Inventory Visuals");
        ImGui.spacing();
        ImGui.separator();
        ImGui.spacing();

        ImGui.setCursorPosX(startX);
        float availableWidth = Math.max(1.0f, ImGui.getContentRegionAvail().x);
        ImGui.pushTextWrapPos(startX + availableWidth);
        ImGui.textDisabled("Inventory highlight settings will be added here.");
        ImGui.popTextWrapPos();
    }
}