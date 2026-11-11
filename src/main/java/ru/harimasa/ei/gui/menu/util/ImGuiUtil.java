package ru.harimasa.ei.gui.menu.util;

import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import ru.harimasa.ei.gui.ImGuiRuntime;

import java.util.function.Consumer;

public final class ImGuiUtil {
    private static final float ROW_STACK_THRESHOLD = 250.0f;
    private static final float LABEL_MIN_WIDTH = 110.0f;
    private static final float LABEL_MAX_WIDTH = 150.0f;
    private static final float CONTROL_MIN_WIDTH = 72.0f;
    private static final float CONTROL_MAX_WIDTH = 213.0f;
    private static final float COLUMN_GAP = 12.0f;
    private static final float FIELD_SPACING_EXTRA = 4.0f;

    private ImGuiUtil() {}

    public static float s(float value) {
        return value * ImGuiRuntime.UI_SCALE;
    }

    public static void pushFieldSpacing() {
        ImGuiStyle style = ImGui.getStyle();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, style.getItemSpacingX(), style.getItemSpacingY() + s(FIELD_SPACING_EXTRA));
    }

    public static void popFieldSpacing() {
        ImGui.popStyleVar();
    }

    public static boolean stackedRows(float availableWidth) {
        return availableWidth < s(ROW_STACK_THRESHOLD);
    }

    public static float labelColumnWidth(float availableWidth) {
        return Math.max(s(LABEL_MIN_WIDTH), Math.min(s(LABEL_MAX_WIDTH), availableWidth * 0.42f));
    }

    public static float controlColumnWidth(float availableWidth) {
        float width = availableWidth - labelColumnWidth(availableWidth) - s(COLUMN_GAP);
        return Math.max(s(CONTROL_MIN_WIDTH), Math.min(s(CONTROL_MAX_WIDTH), width));
    }

    public static void formLabel(String text, float startX) {
        ImGui.setCursorPosX(startX);
        float availableWidth = ImGui.getContentRegionAvail().x;

        ImGui.alignTextToFramePadding();
        ImGui.text(text);
        if (stackedRows(availableWidth)) {
            ImGui.setCursorPosX(startX);
        } else {
            ImGui.sameLine(startX + availableWidth - controlColumnWidth(availableWidth));
        }
    }

    public static void formControlWidth() {
        ImGui.setNextItemWidth(Math.max(s(1.0f), ImGui.getContentRegionAvail().x));
    }

    public static void formCheckbox(String label, String id, boolean value, float startX, Consumer<Boolean> setter) {
        ImGui.setCursorPosX(startX);
        float availableWidth = ImGui.getContentRegionAvail().x;

        ImGui.alignTextToFramePadding();
        ImGui.text(label);
        if (stackedRows(availableWidth)) {
            ImGui.setCursorPosX(startX);
        } else {
            float controlWidth = controlColumnWidth(availableWidth);
            float controlX = startX + availableWidth - controlWidth;
            float frameHeight = ImGui.getFrameHeight();
            ImGui.sameLine(Math.max(controlX, controlX + controlWidth - frameHeight));
        }

        if (ImGui.checkbox(id, value)) {
            setter.accept(!value);
        }
    }

    public static void rowSeparator(float startX) {
        ImGui.setCursorPosX(startX);
        float x = ImGui.getCursorScreenPosX();
        float y = ImGui.getCursorScreenPosY() + s(4.0f);
        float width = Math.max(1.0f, ImGui.getContentRegionAvail().x);

        ImGui.getWindowDrawList().addLine(x, y, x + width, y, ImGui.getColorU32(ImGuiCol.Separator), Math.max(1.0f, s(1.0f)));
        ImGui.dummy(0.0f, s(6.0f));
    }

    public static void note(String text, float startX) {
        ImGui.setCursorPosX(startX);
        float x = ImGui.getCursorPosX();
        float availableWidth = Math.max(1.0f, ImGui.getContentRegionAvail().x);

        ImGui.pushTextWrapPos(x + availableWidth);
        ImGui.textDisabled(text);
        ImGui.popTextWrapPos();
    }

    public static void heading(String text, float startX) {
        ImGui.setCursorPosX(startX);
        ImFont bold = ImGuiRuntime.boldFont();
        if (bold == null) {
            ImGui.text(text);
            return;
        }

        float size = ImGui.getFontSize() + 1.0f;
        ImVec2 textSize = bold.calcTextSizeA(size, Float.MAX_VALUE, 0.0f, text);
        ImGui.getWindowDrawList().addText(
                bold,
                (int) size,
                ImGui.getCursorScreenPosX(),
                ImGui.getCursorScreenPosY(),
                ImGui.getColorU32(ImGuiCol.Text),
                text
        );
        ImGui.dummy(textSize.x, textSize.y);
    }

    public static boolean modeButton(String text, boolean selected, float width) {
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, s(1.0f));
        float[] color;
        if (selected) {
            color = ImGuiTheme.panelColor(0.49f);
            ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], 1.0f);
            color = ImGuiTheme.panelColor(0.68f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], 1.0f);
            color = ImGuiTheme.accentColor();
            ImGui.pushStyleColor(ImGuiCol.Border, color[0], color[1], color[2], 1.0f);
        } else {
            color = ImGuiTheme.borderColor();
            ImGui.pushStyleColor(ImGuiCol.Border, color[0], color[1], color[2], 1.0f);
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