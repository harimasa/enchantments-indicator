package ru.harimasa.ei.gui.menu.component;

import com.mojang.blaze3d.opengl.GlTexture;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.gui.menu.util.ImGuiTheme;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ComponentSidebar {
    private static final String[] PAGES = {"Colors", "Inventory"};
    private static final int SETTINGS_PAGE = 2;

    private ComponentSidebar() {}

    public static void renderVertical(EIConfig config, float width) {
        ImGui.beginChild("sidebar", width, 0.0f, false);

        float padding = s(10.0f);
        float buttonWidth = Math.max(s(40.0f), width - padding * 2.0f);
        float buttonHeight = s(44.0f);
        float gap = s(14.0f);

        ImGui.setCursorPosY(s(14.0f));
        for (int i = 0; i < PAGES.length; i++) {
            ImGui.setCursorPosX(padding);
            if (navButton(PAGES[i], config.page == i, buttonWidth, buttonHeight, false)) {
                config.page = i;
            }
            if (i < PAGES.length - 1) {
                ImGui.dummy(0.0f, gap);
            }
        }

        float settingsY = ImGui.getWindowHeight() - buttonHeight - padding;
        float minSettingsY = ImGui.getCursorPosY() + gap;
        if (settingsY < minSettingsY) {
            settingsY = minSettingsY;
        }

        ImGui.setCursorPos(padding, settingsY);
        if (settingsButton(buttonHeight, config.page == SETTINGS_PAGE)) {
            config.page = SETTINGS_PAGE;
        }
        ImGui.endChild();
    }

    public static void renderHorizontal(EIConfig config, float width) {
        float height = s(34.0f);
        float gap = s(6.0f);
        float settingsSize = height;
        float tabsWidth = Math.max(0.0f, width - settingsSize - gap);
        float buttonWidth = Math.max(s(1.0f), (tabsWidth - gap * (PAGES.length - 1)) / PAGES.length);
        float x = ImGui.getCursorScreenPosX();
        float y = ImGui.getCursorScreenPosY();
        float cursorX = x;
        for (int i = 0; i < PAGES.length; i++) {
            ImGui.setCursorScreenPos(cursorX, y);
            if (navButton(PAGES[i], config.page == i, buttonWidth, height, true)) {
                config.page = i;
            }
            cursorX += buttonWidth + gap;
        }
        ImGui.setCursorScreenPos(x + Math.max(0.0f, width - settingsSize), y);
        if (settingsButton(settingsSize, config.page == SETTINGS_PAGE)) {
            config.page = SETTINGS_PAGE;
        }
        ImGui.setCursorScreenPos(x, y + height);
    }

    private static boolean navButton(String label, boolean selected, float width, float height, boolean horizontal) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        float x = ImGui.getCursorScreenPosX();
        float y = ImGui.getCursorScreenPosY();

        ImGui.invisibleButton("##nav_" + label, width, height);
        boolean clicked = ImGui.isItemClicked();
        boolean hovered = ImGui.isItemHovered();

        int background = selected ? ImGuiTheme.panelColorU32(0.33f) : hovered ? ImGuiTheme.panelColorU32(0.18f) : ImGuiTheme.panelColorU32(0.06f);
        drawList.addRectFilled(x, y, x + width, y + height, background, s(6.0f));

        int accent = ImGuiTheme.accentColorU32();
        if (selected) {
            if (horizontal) {
                drawList.addRectFilled(x, y + height - s(2.0f), x + width, y + height, accent, s(2.0f), ImDrawFlags.RoundCornersBottom);
            } else {
                drawList.addRectFilled(x, y, x + s(4.0f), y + height, accent, s(6.0f), ImDrawFlags.RoundCornersLeft);
            }
        }
        ImVec2 textSize = ImGui.calcTextSize(label);
        int textColor = selected ? ImGui.getColorU32(1.0f, 1.0f, 1.0f, 1.0f) : ImGui.getColorU32(0.75f, 0.75f, 0.8f, 1.0f);
        float iconRadius = s(5.0f);
        float iconDiameter = iconRadius * 2.0f;
        float iconGap = s(12.0f);
        float contentWidth = iconDiameter + iconGap + textSize.x;
        boolean showIcon = !horizontal && contentWidth <= width - s(24.0f);

        drawList.pushClipRect(x + s(2.0f), y, x + Math.max(s(2.0f), width - s(2.0f)), y + height, true);
        if (showIcon) {
            float contentStartX = x + s(18.0f);
            float iconCenterX = contentStartX + iconRadius;
            float iconCenterY = y + height * 0.5f;
            float textX = contentStartX + iconDiameter + iconGap;
            float textY = y + (height - textSize.y) * 0.5f;

            drawIcon(drawList, selected, iconCenterX, iconCenterY, iconRadius);
            drawList.addText(textX, textY, textColor, label);
        } else {
            float textX = x + (width - textSize.x) * 0.5f;
            float textY = y + (height - textSize.y) * 0.5f;
            drawList.addText(textX, textY, textColor, label);
        }
        drawList.popClipRect();
        return clicked;
    }

    private static void drawIcon(ImDrawList drawList, boolean selected, float centerX, float centerY, float radius) {
        int accent = ImGuiTheme.accentColorU32();
        if (selected) {
            drawList.addCircle(centerX, centerY, radius, accent, 16, s(1.5f));
            drawList.addCircleFilled(centerX, centerY, radius * 0.5f, accent, 16);
        } else {
            int muted = ImGui.getColorU32(0.45f, 0.45f, 0.5f, 1.0f);
            drawList.addRect(centerX - radius, centerY - radius, centerX + radius, centerY + radius, muted, s(2.0f), 0, s(1.5f));
            drawList.addRectFilled(centerX - radius / 2.0f, centerY - radius / 2.0f, centerX + radius / 2.0f, centerY + radius / 2.0f, muted, s(1.0f));
        }
    }

    private static boolean settingsButton(float size, boolean selected) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        float x = ImGui.getCursorScreenPosX();
        float y = ImGui.getCursorScreenPosY();

        ImGui.invisibleButton("##settings", size, size);
        boolean clicked = ImGui.isItemClicked();
        boolean hovered = ImGui.isItemHovered();

        int background = selected ? ImGuiTheme.panelColorU32(0.33f) : hovered ? ImGuiTheme.panelColorU32(0.18f) : ImGuiTheme.panelColorU32(0.06f);
        drawList.addRectFilled(x, y, x + size, y + size, background, s(6.0f));
        if (selected) {
            drawList.addRect(x, y, x + size, y + size, ImGuiTheme.accentColorU32(), s(6.0f), 0, Math.max(1.0f, s(1.0f)));
        }

        Identifier gearTexture = Identifier.fromNamespaceAndPath("enchantments-indicator", "textures/gui/settings.png");
        try {
            var texture = Minecraft.getInstance().getTextureManager().getTexture(gearTexture);
            int texId = ((GlTexture) texture.getTexture()).glId();
            float padding = s(7.0f);
            float imageSize = Math.max(1.0f, size - padding * 2.0f);
            drawList.addImage(texId, x + padding, y + padding, x + padding + imageSize, y + padding + imageSize);
        } catch (Exception e) {
            float centerX = x + size * 0.5f;
            float centerY = y + size * 0.5f;
            int muted = ImGui.getColorU32(0.75f, 0.75f, 0.8f, 1.0f);
            drawList.addCircle(centerX, centerY, s(6.0f), muted, 16, Math.max(1.0f, s(2.0f)));
            drawList.addCircleFilled(centerX, centerY, s(2.0f), muted, 16);
        }
        return clicked;
    }
}