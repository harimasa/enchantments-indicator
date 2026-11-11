package ru.harimasa.ei.gui;

/* На классы это все потом расхуярить а то это пиздец страшно выглядит фу сука */

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.platform.InputConstants;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImBoolean;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class ImGuiConfigScreen extends Screen {

    private int page = 0;
    private boolean textureMode = true;
    private final ImBoolean gradient = new ImBoolean(false);
    private boolean modEnabled = true;
    private final float[] mainColor = { 0.75f, 0.33f, 1.0f };
    private float uiScalePercent = 100.0f;

    private int enchantment = 0;

    private final float[] tint = {
            0.67f, 0.33f, 1.0f, 1.0f
    };

    private static final int THEME_COLOR_COUNT = 23;

    public ImGuiConfigScreen() {
        super(Component.literal("Enchantment Indicator"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0x88000000);
        ImGuiRuntime.beginFrame(width, height);
        ImGui.getIO().addMousePosEvent((float) mouseX, (float) mouseY);

        pushTheme();
        drawMenu();

        ImGui.popStyleColor(THEME_COLOR_COUNT);
        ImGui.popStyleVar(7);
        ImGuiRuntime.endFrame();
    }

    // тут наверно(пока что)
    private void pushTheme() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, s(12));
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, s(1));
        ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, s(6));
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, s(4));
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, s(2), s(2));
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, s(10), s(7));
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, s(8), s(8));
        ImGui.pushStyleColor(ImGuiCol.Text, 0.91f, 0.91f, 0.95f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.TextDisabled, 0.53f, 0.54f, 0.61f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.045f, 0.047f, 0.058f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.045f, 0.047f, 0.058f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.PopupBg, 0.075f, 0.077f, 0.095f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.20f, 0.21f, 0.25f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.085f, 0.09f, 0.115f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.14f, 0.13f, 0.18f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0.18f, 0.14f, 0.23f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.TitleBg, 0.045f, 0.047f, 0.058f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.TitleBgActive, 0.045f, 0.047f, 0.058f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.085f, 0.09f, 0.115f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.17f, 0.15f, 0.22f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.23f, 0.16f, 0.29f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.Header, 0.11f, 0.12f, 0.16f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0.17f, 0.15f, 0.22f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0.23f, 0.13f, 0.30f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.CheckMark, 0.75f, 0.33f, 1.0f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.SliderGrab, 0.70f, 0.30f, 0.95f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.SliderGrabActive, 0.85f, 0.45f, 1.0f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.Separator, 0.19f, 0.20f, 0.24f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.SeparatorHovered, 0.65f, 0.30f, 0.90f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.SeparatorActive, 0.75f, 0.33f, 1.0f, 1.0f);
    }

    // TODO: gui/menu
    private void drawMenu() {
        float windowWidth = Math.min(s(610), width - s(32));
        float windowHeight = Math.min(s(342), height - s(32));

        ImGui.setNextWindowPos((width - windowWidth) * 0.5f, (height - windowHeight) * 0.5f, ImGuiCond.Once);
        ImGui.setNextWindowSize(windowWidth, windowHeight, ImGuiCond.Always);
        int flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoScrollbar;
        if (ImGui.begin("##enchantment_indicator", flags)) {
            drawHeader();
            ImGui.separator();
            drawSidebar();
            ImGui.sameLine();

            float lineX = ImGui.getCursorScreenPosX() - s(4);
            float lineY1 = ImGui.getCursorScreenPosY();
            float lineY2 = lineY1 + ImGui.getContentRegionAvail().y;
            ImGui.getWindowDrawList().addLine(lineX, lineY1, lineX, lineY2, ImGui.getColorU32(ImGuiCol.Separator), 1.0f);

            drawContent();
        }
        ImGui.end();
    }

    // TODO: gui/menu/component/ComponentHeader
    private void drawHeader() {
        ImGui.beginChild("header", 0, s(45), false);
        ImGui.setCursorPos(s(18), s(13));
        ImGui.textColored(0.75f, 0.35f, 1.0f, 1.0f, "✧");

        ImGui.sameLine();

        ImGui.setWindowFontScale(1.15f);
        ImGui.textColored(1.0f, 1.0f, 1.0f, 1.0f, "Enchantment Indicator");
        ImGui.setWindowFontScale(1.0f);

        ImGui.setCursorPos(s(570), s(10));

        if (ImGui.button("X", s(25), s(24))) MinecraftClose.request();
        ImGui.endChild();
    }

    // TODO: gui/menu/component/ComponentSidebar
    private void drawSidebar() {
        ImGui.beginChild("sidebar", s(152), 0, true);

        float padding = s(12);
        float btnWidth = s(152) - padding * 2;
        float btnHeight = s(38);

        ImGui.setCursorPosY(s(20));
        String[] names = {"Colors", "Inventory"};

        for (int i = 0; i < names.length; i++) {
            boolean selected = page == i;

            ImGui.setCursorPosX(padding);
            if (customSidebarButton(names[i], selected, btnWidth, btnHeight)) {
                page = i;
            }
            ImGui.spacing();
        }
        float bottomY = ImGui.getWindowHeight() - btnHeight - padding;
        ImGui.setCursorPos(padding, bottomY);

        if (customGearButton(btnHeight, page == 2)) {
            page = 2;
        }

        ImGui.endChild();
    }

    // TODO: gui/menu/component/ComponentSidebar
    private boolean customGearButton(float size, boolean selected) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        float x = ImGui.getCursorScreenPosX();
        float y = ImGui.getCursorScreenPosY();

        ImGui.invisibleButton("##settings", size, size);
        boolean clicked = ImGui.isItemClicked();
        boolean hovered = ImGui.isItemHovered();

        int bgCol;
        if (selected) {
            bgCol = ImGui.getColorU32(0.13f, 0.12f, 0.16f, 1.0f);
        } else if (hovered) {
            bgCol = ImGui.getColorU32(0.09f, 0.09f, 0.11f, 1.0f);
        } else {
            bgCol = ImGui.getColorU32(0.06f, 0.06f, 0.08f, 1.0f);
        }

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

    // думаю тут или gui/menu/component/sub/ComponentSubSettings
    private boolean customSidebarButton(String label, boolean selected, float width, float height) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        float x = ImGui.getCursorScreenPosX();
        float y = ImGui.getCursorScreenPosY();

        ImGui.invisibleButton("##btn_" + label, width, height);
        boolean clicked = ImGui.isItemClicked();
        boolean hovered = ImGui.isItemHovered();

        int bgCol;
        if (selected) {
            bgCol = ImGui.getColorU32(0.13f, 0.12f, 0.16f, 1.0f);
        } else if (hovered) {
            bgCol = ImGui.getColorU32(0.09f, 0.09f, 0.11f, 1.0f);
        } else {
            bgCol = ImGui.getColorU32(0.06f, 0.06f, 0.08f, 1.0f);
        }
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

    // here
    private void drawContent() {
        ImGui.beginChild("content", 0, 0, false);
        ImGui.setCursorPos(s(20), s(18));
        if (page == 0) drawColors();
        else if (page == 1) drawInventory();
        else if (page == 2) drawGeneralSettings();

        ImGui.endChild();
    }

    // TODO: gui/menu/component/ComponentSettings
    private void drawGeneralSettings() {
        float startX = ImGui.getCursorPosX();
        float rightColumnX = startX + s(190);
        float elementWidth = s(213);

        ImGui.setCursorPosX(startX);
        ImGui.text("General Settings");
        ImGui.spacing();
        ImGui.separator();

        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();
        ImGui.text("Enable Mod");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);
        ImBoolean modEnabledBool = new ImBoolean(modEnabled);
        if (ImGui.checkbox("##modEnabled", modEnabledBool)) {
            modEnabled = modEnabledBool.get();
        }
        ImGui.setCursorPosX(startX);
        ImGui.separator();

        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();
        ImGui.text("Main Mod Color");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);
        ImGui.setNextItemWidth(elementWidth);
        ImGui.colorEdit3("##mainColor", mainColor);
        ImGui.setCursorPosX(startX);
        ImGui.separator();

        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();
        ImGui.text("UI Scale");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);
        ImGui.setNextItemWidth(elementWidth);
        float[] scaleArr = {uiScalePercent};
        if (ImGui.sliderFloat("##uiScale", scaleArr, 0.0f, 100.0f, "%.0f%%")) {
            uiScalePercent = scaleArr[0];
            ImGuiRuntime.UI_SCALE = uiScalePercent / 100.0f;
        }
        ImGui.setCursorPosX(startX);
        ImGui.separator();
    }

    // TODO: gui/menu/component/sub/ComponentSubColors
    private void drawColors() {
        float startX = ImGui.getCursorPosX();

        ImGui.setCursorPosX(startX);
        ImGui.text("Render Mode");
        ImGui.spacing();

        float rightColumnX = startX + s(190);
        float elementWidth = s(213);
        float rightEdge = rightColumnX + elementWidth;
        float buttonSpacing = s(8);
        float buttonWidth = (rightEdge - startX - buttonSpacing) / 2.0f;

        ImGui.setCursorPosX(startX);
        if (modeButton("Tint", !textureMode, buttonWidth)) textureMode = false;
        ImGui.sameLine(0, buttonSpacing);
        if (modeButton("Texture", textureMode, buttonWidth)) textureMode = true;
        ImGui.spacing();
        ImGui.setCursorPosX(startX);
        ImGui.separator();
        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();

        ImGui.text("Enchantment");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);
        String[] modes = {
                "Protection",
                "Fire Protection",
                "Blast Protection",
                "Projectile Protection",
                "Unbreaking",
                "Mending",
                "Thorns",
                "Curse of Binding",
                "Curse of Vanishing",
                "Respiration",
                "Aqua Affinity",
                "Swift Sneak",
                "Feather Falling",
                "Depth Strider",
                "Frost Walker",
                "Soul Speed"
        };
        ImGui.setNextItemWidth(elementWidth);
        if (ImGui.beginCombo("##enchantment", modes[enchantment])) {
            for (int i = 0; i < modes.length; i++) {
                if (ImGui.selectable(modes[i], enchantment == i)) {
                    enchantment = i;
                }
            }
            ImGui.endCombo();
        }
        ImGui.setCursorPosX(startX);
        ImGui.separator();
        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.95f);
        ImGui.alignTextToFramePadding();

        ImGui.text("Enchantment Color");
        ImGui.setWindowFontScale(1.0f);
        ImGui.sameLine(rightColumnX);
        ImGui.setNextItemWidth(elementWidth);
        if (textureMode) {
            float[] rgbTint = new float[] { tint[0], tint[1], tint[2] };
            if (ImGui.colorEdit3("##tint", rgbTint)) {
                tint[0] = rgbTint[0];
                tint[1] = rgbTint[1];
                tint[2] = rgbTint[2];
            }
        } else {
            ImGui.colorEdit4("##tint", tint);
        }
        ImGui.setCursorPosX(startX);
        ImGui.separator();

        if (!textureMode) {
            ImGui.setCursorPosX(startX);
            ImGui.setWindowFontScale(0.95f);
            ImGui.alignTextToFramePadding();
            ImGui.text("Gradient");
            ImGui.setWindowFontScale(1.0f);
            float checkboxSize = ImGui.getFrameHeight();
            ImGui.sameLine(rightEdge - checkboxSize);
            ImGui.checkbox("##gradient", gradient);
            ImGui.setCursorPosX(startX);
            ImGui.separator();
        }

        ImGui.spacing();
        ImGui.setCursorPosX(startX);
        ImGui.setWindowFontScale(0.85f);
        ImGui.textDisabled("Changes are applied immediately while playing.");
        ImGui.setWindowFontScale(1.0f);
    }

    private boolean modeButton(String text, boolean selected, float width) {
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, s(1));
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0.17f, 0.10f, 0.23f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.22f, 0.12f, 0.30f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.Border, 0.75f, 0.30f, 1.0f, 1.0f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Border, 0.20f, 0.21f, 0.25f, 1.0f);
        }
        boolean clicked = ImGui.button(text, width, s(30));

        if (selected) ImGui.popStyleColor(3);
        else ImGui.popStyleColor(1);

        ImGui.popStyleVar(1);
        return clicked;
    }

    // TODO: gui/menu/component/sub/ComponentSubInventory
    private void drawInventory() {
        ImGui.text("Inventory Visuals");
        ImGui.spacing();
        ImGui.separator();
        ImGui.textDisabled("Inventory highlight settings will be added here.");
    }

    private static float s(float value) {
        return value * ImGuiRuntime.UI_SCALE;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        ImGui.getIO().addMousePosEvent((float) event.x(), (float) event.y());
        ImGui.getIO().addMouseButtonEvent(event.button(), true);
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        ImGui.getIO().addMousePosEvent((float) event.x(), (float) event.y());
        ImGui.getIO().addMouseButtonEvent(event.button(), false);
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        ImGui.getIO().addMousePosEvent((float) event.x(), (float) event.y());
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        ImGui.getIO().addMousePosEvent((float) mouseX, (float) mouseY);
        ImGui.getIO().addMouseWheelEvent((float) horizontal, (float) vertical);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        MinecraftClose.request();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final class MinecraftClose {
        static void request() {
            Minecraft.getInstance().setScreen(null);
        }
    }
}