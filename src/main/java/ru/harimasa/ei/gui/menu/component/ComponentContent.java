package ru.harimasa.ei.gui.menu.component;

import imgui.ImGui;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.gui.menu.component.sub.ComponentSubColors;
import ru.harimasa.ei.gui.menu.component.sub.ComponentSubInventory;
import ru.harimasa.ei.gui.menu.component.sub.ComponentSubSettings;
import ru.harimasa.ei.gui.menu.util.ImGuiUtil;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ComponentContent {
    private ComponentContent() {}

    public static void render(EIConfig config, boolean compact) {
        float availableWidth = ImGui.getContentRegionAvail().x;
        float padding = Math.min(s(18.0f), Math.max(s(8.0f), availableWidth * 0.06f));
        float rightGap = padding + (compact ? 0.0f : s(2.0f));

        ImGui.beginChild("content", -rightGap, 0.0f, false);
        ImGui.setCursorPos(padding, s(18.0f));

        ImGuiUtil.pushFieldSpacing();
        if (config.page == 0) ComponentSubColors.render(config);
        else if (config.page == 1) ComponentSubInventory.render(config);
        else if (config.page == 2) ComponentSubSettings.render(config);
        ImGuiUtil.popFieldSpacing();

        ImGui.endChild();
    }
}