package ru.harimasa.ei.gui.menu.component;

import com.mojang.blaze3d.opengl.GlTexture;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import ru.harimasa.ei.config.EIConfig;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public class ComponentSidebar {
    public static void render(EIConfig config) {
        ImGui.beginChild("sidebar", s(152), 0, true);
        float padding = s(12);
        float btnWidth = s(152) - padding * 2;
        float btnHeight = s(38);

        ImGui.setCursorPosY(s(20));
        String[] names = {"Colors", "Inventory"};
        for (int i = 0; i < names.length; i++) {
            boolean selected = config.page == i;
            ImGui.setCursorPosX(padding);
            if (customSidebarButton(names[i], selected, btnWidth, btnHeight)) {
                config.page = i;
            }
            ImGui.spacing();
        }

        float bottomY = ImGui.getWindowHeight() - btnHeight - padding;
        ImGui.setCursorPos(padding, bottomY);
        if (customSettingsButton(btnHeight, config.page == 2)) {
            config.page = 2;
        }
        ImGui.endChild();
    }

    private static boolean customSettingsButton(float size, boolean selected) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        float x = ImGui.getCursorScreenPosX();
        float y = ImGui.getCursorScreenPosY();
        ImGui.invisibleButton("##settings", size, size);
        boolean clicked = ImGui.isItemClicked();
        boolean hovered = ImGui.isItemHovered();

        int bgCol = selected ? ImGui.getColorU32(0.13f, 0.12f, 0.16f, 1.0f) :
                hovered  ? ImGui.getColorU32(0.09f, 0.09f, 0.11f, 1.0f) :
                        ImGui.getColorU32(0.06f, 0.06f, 0.08f, 1.0f);
        drawList.addRectFilled(x, y, x + size, y + size, bgCol, s(6));

        Identifier gearTexture = Identifier.fromNamespaceAndPath("enchantments-indicator", "textures/gui/settings.png");
        try {
            var texture = Minecraft.getInstance().getTextureManager().getTexture(gearTexture);
            int texId = ((GlTexture) texture.getTexture()).glId();
            float padding = s(8.0f);
            float imgSize = size - (padding * 2);
            drawList.addImage(texId, x + padding, y + padding, x + padding + imgSize, y + padding + imgSize);
        } catch (Exception e) {
            float cx = x + size / 2.0f;
            float cy = y + size / 2.0f;
            drawList.addCircle(cx, cy, s(6.0f), ImGui.getColorU32(0.75f, 0.75f, 0.8f, 1.0f), 16, s(2.0f));
        }
        return clicked;
    }

    private static boolean customSidebarButton(String label, boolean selected, float width, float height) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        float x = ImGui.getCursorScreenPosX();
        float y = ImGui.getCursorScreenPosY();
        ImGui.invisibleButton("##btn_" + label, width, height);
        boolean clicked = ImGui.isItemClicked();
        boolean hovered = ImGui.isItemHovered();

        int bgCol = selected ? ImGui.getColorU32(0.13f, 0.12f, 0.16f, 1.0f) :
                hovered  ? ImGui.getColorU32(0.09f, 0.09f, 0.11f, 1.0f) :
                        ImGui.getColorU32(0.06f, 0.06f, 0.08f, 1.0f);
        drawList.addRectFilled(x, y, x + width, y + height, bgCol, s(6));

        if (selected) drawList.addRectFilled(x, y, x + s(4), y + height, ImGui.getColorU32(0.75f, 0.33f, 1.0f, 1.0f), s(6), ImDrawFlags.RoundCornersLeft);

        ImVec2 textSize = ImGui.calcTextSize(label);
        float iconRadius = s(5);
        float iconWidth = iconRadius * 2;
        float spacing = s(14);
        float contentStartX = x + s(20);
        float iconCenterX = contentStartX + iconRadius;
        float iconCenterY = y + height / 2.0f;
        float textX = contentStartX + iconWidth + spacing;
        float textY = y + (height - textSize.y) / 2.0f;

        if (selected) {
            drawList.addCircle(iconCenterX, iconCenterY, iconRadius, ImGui.getColorU32(0.75f, 0.33f, 1.0f, 1.0f), 16, s(1.5f));
            drawList.addCircleFilled(iconCenterX, iconCenterY, iconRadius * 0.5f, ImGui.getColorU32(0.75f, 0.33f, 1.0f, 1.0f), 16);
        } else {
            drawList.addRect(iconCenterX - iconRadius, iconCenterY - iconRadius, iconCenterX + iconRadius, iconCenterY + iconRadius, ImGui.getColorU32(0.45f, 0.45f, 0.5f, 1.0f), s(2), 0, s(1.5f));
            drawList.addRectFilled(iconCenterX - iconRadius/2, iconCenterY - iconRadius/2, iconCenterX + iconRadius/2, iconCenterY + iconRadius/2, ImGui.getColorU32(0.45f, 0.45f, 0.5f, 1.0f), s(1));
        }

        int textCol = selected ? ImGui.getColorU32(1.0f, 1.0f, 1.0f, 1.0f) : ImGui.getColorU32(0.75f, 0.75f, 0.8f, 1.0f);
        drawList.addText(textX, textY, textCol, label);
        return clicked;
    }
}