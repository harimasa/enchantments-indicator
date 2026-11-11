package ru.harimasa.ei.gui.menu.util;

import ru.harimasa.ei.gui.ImGuiRuntime;

public class ImGuiUtil {
    public static float s(float value) {
        return value * ImGuiRuntime.UI_SCALE;
    }
}