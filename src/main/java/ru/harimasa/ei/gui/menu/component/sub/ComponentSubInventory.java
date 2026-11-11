package ru.harimasa.ei.gui.menu.component.sub;

import imgui.ImGui;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.enums.RenderMode;
import ru.harimasa.ei.gui.menu.util.ImGuiUtil;
import ru.harimasa.ei.lang.Lang;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ComponentSubInventory {
    private ComponentSubInventory() {}

    public static void render(EIConfig config) {
        float startX = ImGui.getCursorPosX();

        ImGui.setCursorPosX(startX);
        ImGuiUtil.heading(Lang.get("ei.inventory.title"), startX);
        ImGui.spacing();

        ImGuiUtil.formCheckbox(Lang.get("ei.inventory.enable"), "##inventoryEnabled", config.inventory.enabled, startX,
                value -> {
                    config.inventory.enabled = value;
                    ConfigManager.markDirty();
                });

        ImGuiUtil.formLabel(Lang.get("ei.inventory.render_mode"), startX);
        float availableWidth = ImGui.getContentRegionAvail().x;
        float buttonGap = s(8.0f);
        float buttonWidth = Math.max(s(40.0f), (availableWidth - buttonGap) * 0.5f);

        if (ImGuiUtil.modeButton(Lang.get("ei.colors.mode.glint"), config.inventory.renderMode == RenderMode.GLINT, buttonWidth)) {
            config.inventory.renderMode = RenderMode.GLINT;
            ConfigManager.markDirty();
        }
        ImGui.sameLine(0.0f, buttonGap);
        if (ImGuiUtil.modeButton(Lang.get("ei.colors.mode.texture"), config.inventory.renderMode == RenderMode.TEXTURE, buttonWidth)) {
            config.inventory.renderMode = RenderMode.TEXTURE;
            ConfigManager.markDirty();
        }

        ImGuiUtil.rowSeparator(startX);

        ImGui.spacing();
        ImGuiUtil.note(Lang.get(config.inventory.renderMode == RenderMode.GLINT ? "ei.inventory.note.glint" : "ei.inventory.note.texture"), startX);
    }
}