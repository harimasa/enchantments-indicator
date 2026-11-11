package ru.harimasa.ei.gui.menu.component;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import net.minecraft.client.Minecraft;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ComponentHeader {
    private static final String TITLE = "Enchantments Indicator";
    private static final String ICON = "✧";

    private ComponentHeader() {}

    public static void render(float width) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        float x = ImGui.getCursorScreenPosX();
        float y = ImGui.getCursorScreenPosY();
        float height = s(48.0f);
        float padding = s(18.0f);
        float closeSize = Math.max(1.0f, Math.round(s(26.0f)));
        float closeX = Math.round(x + Math.max(0.0f, width - closeSize - s(12.0f)));
        float closeY = Math.round(y + (height - closeSize) * 0.5f);

        ImFont font = ImGui.getFont();
        int titleFontSize = Math.max(1, Math.round(ImGui.getFontSize() * 1.15f));
        ImVec2 iconSize = font.calcTextSizeA(titleFontSize, 10000.0f, 0.0f, ICON);
        ImVec2 titleSize = font.calcTextSizeA(titleFontSize, 10000.0f, 0.0f, TITLE);

        float iconX = x + padding;
        float iconY = y + (height - iconSize.y) * 0.5f;
        float titleX = iconX + iconSize.x + s(8.0f);
        float titleY = y + (height - titleSize.y) * 0.5f;
        float clipRight = Math.max(iconX, closeX - s(10.0f));
        drawList.pushClipRect(iconX, y, clipRight, y + height, true);
        drawList.addText(font, titleFontSize, iconX, iconY, ImGui.getColorU32(0.75f, 0.35f, 1.0f, 1.0f), ICON);
        drawList.addText(font, titleFontSize, titleX, titleY, ImGui.getColorU32(1.0f, 1.0f, 1.0f, 1.0f), TITLE);
        drawList.popClipRect();

        ImGui.setCursorScreenPos(closeX, closeY);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.55f, 0.16f, 0.24f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.72f, 0.20f, 0.30f, 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, s(6.0f));

        boolean closeClicked = ImGui.button("##close", closeSize, closeSize);
        boolean closeHovered = ImGui.isItemHovered();

        ImGui.popStyleVar(1);
        ImGui.popStyleColor(3);

        float inset = Math.round(closeSize * 0.33f);
        int closeColor = closeHovered ? ImGui.getColorU32(1.0f, 1.0f, 1.0f, 1.0f) : ImGui.getColorU32(0.78f, 0.78f, 0.84f, 1.0f);
        float crossThickness = Math.max(1.0f, s(1.5f));
        drawList.addLine(closeX + inset, closeY + inset, closeX + closeSize - inset, closeY + closeSize - inset, closeColor, crossThickness);
        drawList.addLine(closeX + inset, closeY + closeSize - inset, closeX + closeSize - inset, closeY + inset, closeColor, crossThickness);

        if (closeClicked) Minecraft.getInstance().setScreen(null);
        ImGui.setCursorScreenPos(x, y + height);
    }
}