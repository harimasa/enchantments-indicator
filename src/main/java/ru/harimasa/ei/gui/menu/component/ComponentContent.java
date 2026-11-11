package ru.harimasa.ei.gui.menu.component;

import imgui.ImGui;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.gui.menu.component.sub.ComponentSubColors;
import ru.harimasa.ei.gui.menu.component.sub.ComponentSubInventory;
import ru.harimasa.ei.gui.menu.component.sub.ComponentSubSettings;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public class ComponentContent {
    public static void render(EIConfig config) {
        ImGui.beginChild("content", 0, 0, false);
        ImGui.setCursorPos(s(20), s(18));

        if (config.page == 0) ComponentSubColors.render(config);
        else if (config.page == 1) ComponentSubInventory.render();
        else if (config.page == 2) ComponentSubSettings.render(config);

        ImGui.endChild();
    }
}