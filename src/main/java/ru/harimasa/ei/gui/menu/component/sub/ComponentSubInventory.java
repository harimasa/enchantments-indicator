package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.RenderMode;
import ru.harimasa.ei.gui.menu.util.ImGuiUtil;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ComponentSubInventory {
    private ComponentSubInventory() {}

    public static void render(EIConfig config) {
        float startX = ImGui.getCursorPosX();

        ImGui.setCursorPosX(startX);
        ImGui.text("Inventory Visuals");
        ImGui.spacing();

        ImGuiUtil.formCheckbox("Enable", "##inventoryEnabled", config.inventory.enabled, startX,
                value -> {
                    config.inventory.enabled = value;
                    ConfigManager.markDirty();
                });
        ImGuiUtil.rowSeparator(startX);

        ImGuiUtil.formLabel("Render Mode", startX);
        float availableWidth = ImGui.getContentRegionAvail().x;
        float buttonGap = s(8.0f);
        float buttonWidth = Math.max(s(40.0f), (availableWidth - buttonGap) * 0.5f);

        if (ImGuiUtil.modeButton("Glint", config.inventory.renderMode == RenderMode.TINT, buttonWidth)) {
            config.inventory.renderMode = RenderMode.TINT;
            ConfigManager.markDirty();
        }
        ImGui.sameLine(0.0f, buttonGap);
        if (ImGuiUtil.modeButton("Texture", config.inventory.renderMode == RenderMode.TEXTURE, buttonWidth)) {
            config.inventory.renderMode = RenderMode.TEXTURE;
            ConfigManager.markDirty();
        }

        ImGuiUtil.rowSeparator(startX);

        ImGui.spacing();
        ImGuiUtil.note("Placeholder for now." + " Inventory highlighter in the future", startX);
    }
}