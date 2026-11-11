package ru.harimasa.ei.gui.menu.util;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiStyleVar;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;
import org.lwjgl.opengl.GL13C;
import ru.harimasa.ei.config.enums.LanguageOption;
import ru.harimasa.ei.gui.ImGuiRuntime;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ImGuiUtil {
    private static final float ROW_STACK_THRESHOLD = 250.0f;
    private static final float LABEL_MIN_WIDTH = 110.0f;
    private static final float LABEL_MAX_WIDTH = 150.0f;
    private static final float CONTROL_MIN_WIDTH = 72.0f;
    private static final float CONTROL_MAX_WIDTH = 213.0f;
    private static final float COLUMN_GAP = 12.0f;
    private static final float FIELD_SPACING_EXTRA = 4.0f;

    private static final float FLAG_U0 = 0.0f;
    private static final float FLAG_V0 = 88.0f / 512.0f;
    private static final float FLAG_U1 = 1.0f;
    private static final float FLAG_V1 = 424.0f / 512.0f;
    private static final float FLAG_ASPECT = 512.0f / 336.0f;

    private static final Map<LanguageOption, Integer> FLAG_TEXTURE_IDS = new EnumMap<>(LanguageOption.class);

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
        ImGui.getWindowDrawList().addText(bold, (int) size, ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(), ImGui.getColorU32(ImGuiCol.Text), text);
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

        if (selected) ImGui.popStyleColor(3);
        else ImGui.popStyleColor(1);
        ImGui.popStyleVar(1);
        return clicked;
    }

    public static LanguageOption languageDropdown(String id, LanguageOption current, float width) {
        LanguageOption picked = null;
        float height = Math.max(s(24.0f), ImGui.getFrameHeight());
        float flagHeight = s(13.5f);
        float flagWidth = flagHeight * FLAG_ASPECT;
        float padding = s(9.0f);
        float gap = s(8.0f);
        ImDrawList drawList = ImGui.getWindowDrawList();
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, s(1.0f));
        boolean open = ImGui.button("##" + id, width, height);
        float x0 = ImGui.getItemRectMinX();
        float y0 = ImGui.getItemRectMinY();
        float x1 = ImGui.getItemRectMaxX();
        float y1 = ImGui.getItemRectMaxY();
        drawFlag(drawList, current, x0 + padding, y0 + (height - flagHeight) * 0.5f, flagWidth, flagHeight);
        drawList.addText(x0 + padding + flagWidth + gap, y0 + (height - ImGui.getTextLineHeight()) * 0.5f, ImGui.getColorU32(ImGuiCol.Text), current.displayName());

        float arrowX = x1 - padding;
        float arrowY = (y0 + y1) * 0.5f;
        drawList.addTriangleFilled(arrowX - s(4.0f), arrowY - s(2.0f), arrowX + s(1.0f), arrowY - s(2.0f), arrowX - s(1.5f), arrowY + s(2.5f), ImGui.getColorU32(ImGuiCol.TextDisabled));
        ImGui.popStyleVar(1);
        if (open) ImGui.openPopup(id);

        ImGui.setNextWindowSizeConstraints(width, 0.0f, width, Float.MAX_VALUE);
        if (ImGui.beginPopup(id)) {
            ImDrawList popupDrawList = ImGui.getWindowDrawList();
            float itemHeight = height + s(4.0f);
            for (LanguageOption option : LanguageOption.values()) {
                ImGui.pushID(option.code());
                if (ImGui.selectable("##item", option == current, ImGuiSelectableFlags.None, 0.0f, itemHeight)) picked = option;
                float itemMinX = ImGui.getItemRectMinX();
                float itemMinY = ImGui.getItemRectMinY();
                float itemMaxY = ImGui.getItemRectMaxY();
                float actualHeight = itemMaxY - itemMinY;
                drawFlag(popupDrawList, option, itemMinX + padding, itemMinY + (actualHeight - flagHeight) * 0.5f, flagWidth, flagHeight);
                popupDrawList.addText(itemMinX + padding + flagWidth + gap, itemMinY + (actualHeight - ImGui.getTextLineHeight()) * 0.5f,
                        ImGui.getColorU32(ImGuiCol.Text), option.displayName());
                ImGui.popID();
            }
            ImGui.endPopup();
        }
        return picked;
    }

    private static void drawFlag(ImDrawList drawList, LanguageOption language, float x, float y, float width, float height) {
        int textureId = flagTextureId(language);
        if (textureId <= 0) return;
        drawList.addImage(textureId, x, y, x + width, y + height, FLAG_U0, FLAG_V0, FLAG_U1, FLAG_V1);
    }

    private static int flagTextureId(LanguageOption language) {
        try {
            Identifier textureId = Identifier.fromNamespaceAndPath("enchantments-indicator", language.texturePath());
            var texture = Minecraft.getInstance().getTextureManager().getTexture(textureId);
            GlTexture glTexture = (GlTexture) texture.getTexture();
            int id = glTexture.glId();
            if (!Integer.valueOf(id).equals(FLAG_TEXTURE_IDS.get(language))) {
                configureFlagTexture(id);
                FLAG_TEXTURE_IDS.put(language, id);
            }
            return id;
        } catch (Throwable t) {
            return -1;
        }
    }

    private static void configureFlagTexture(int id) {
        int previousActive = GlStateManager._getInteger(GL13C.GL_ACTIVE_TEXTURE);
        GlStateManager._activeTexture(GL13C.GL_TEXTURE0);
        int previousBinding = GlStateManager._getInteger(GL11C.GL_TEXTURE_BINDING_2D);
        GlStateManager._bindTexture(id);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);
        GlStateManager._bindTexture(previousBinding);
        GlStateManager._activeTexture(previousActive);
    }
}