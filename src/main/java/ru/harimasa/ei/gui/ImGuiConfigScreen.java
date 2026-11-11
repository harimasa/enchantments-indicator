package ru.harimasa.ei.gui;

import com.mojang.blaze3d.platform.InputConstants;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiWindowFlags;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.gui.menu.component.ComponentContent;
import ru.harimasa.ei.gui.menu.component.ComponentHeader;
import ru.harimasa.ei.gui.menu.component.ComponentSidebar;
import ru.harimasa.ei.gui.menu.util.ImGuiTheme;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ImGuiConfigScreen extends Screen {

    private static final float MAX_WINDOW_WIDTH = 610.0f;
    private static final float MAX_WINDOW_HEIGHT = 342.0f;
    private static final float MIN_WINDOW_WIDTH = 260.0f;
    private static final float MIN_WINDOW_HEIGHT = 200.0f;

    private static final float WINDOW_BG_INSET = 0.75f;

    private final EIConfig config = new EIConfig();

    private int lastScreenWidth = -1;
    private int lastScreenHeight = -1;

    private float windowX = Float.NaN;
    private float windowY = Float.NaN;
    private boolean draggingWindow;

    public ImGuiConfigScreen() {
        super(Component.literal("Enchantments Indicator"));
        config.uiScalePercent = Math.max(50.0f, Math.min(100.0f, Math.round(ImGuiRuntime.UI_SCALE * 100.0f)));
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0x88000000);
        ImGuiRuntime.beginFrame(width, height, mouseX, mouseY);
        ImGuiTheme.pushTheme();
        drawMenu();
        ImGui.popStyleColor(ImGuiTheme.THEME_COLOR_COUNT);
        ImGui.popStyleVar(7);
        ImGuiRuntime.endFrame();
    }

    private void drawMenu() {
        float windowWidth = Math.min(s(MAX_WINDOW_WIDTH), Math.max(s(MIN_WINDOW_WIDTH), width - s(32.0f)));
        float windowHeight = Math.min(s(MAX_WINDOW_HEIGHT), Math.max(s(MIN_WINDOW_HEIGHT), height - s(32.0f)));
        windowWidth = Math.min(windowWidth, Math.max(1.0f, width - s(8.0f)));
        windowHeight = Math.min(windowHeight, Math.max(1.0f, height - s(8.0f)));

        boolean screenChanged = width != lastScreenWidth || height != lastScreenHeight;
        if (screenChanged || Float.isNaN(windowX) || Float.isNaN(windowY)) {
            windowX = (width - windowWidth) * 0.5f;
            windowY = (height - windowHeight) * 0.5f;
            lastScreenWidth = width;
            lastScreenHeight = height;
        }

        if (draggingWindow) {
            if (!ImGui.isMouseDown(0)) {
                draggingWindow = false;
            } else {
                windowX += ImGui.getIO().getMouseDeltaX();
                windowY += ImGui.getIO().getMouseDeltaY();
            }
        }
        windowX = Math.max(0.0f, Math.min(windowX, Math.max(0.0f, width - windowWidth)));
        windowY = Math.max(0.0f, Math.min(windowY, Math.max(0.0f, height - windowHeight)));

        ImGui.setNextWindowPos(windowX, windowY, ImGuiCond.Always);
        ImGui.setNextWindowSize(windowWidth, windowHeight, ImGuiCond.Always);

        int flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoMove;
        if (!ImGui.begin("##enchantments_indicator", flags)) {
            ImGui.end();
            return;
        }
        drawWindowBackground();

        float contentWidth = ImGui.getContentRegionAvail().x;
        ComponentHeader.render(contentWidth);

        contentWidth = ImGui.getContentRegionAvail().x;
        boolean compact = contentWidth < s(460.0f);
        if (compact) {
            ImGui.spacing();
            ComponentSidebar.renderHorizontal(config, contentWidth);
            ImGui.spacing();
            ImGui.separator();
            ImGui.spacing();
            ComponentContent.render(config, true);
        } else {
            float headerLineY = Math.round(ImGui.getCursorScreenPosY());
            float headerLineX0 = ImGui.getWindowPosX();
            float headerLineX1 = headerLineX0 + ImGui.getWindowWidth();
            float sidebarWidth = Math.min(s(152.0f), Math.max(s(130.0f), contentWidth * 0.30f));

            ComponentSidebar.renderVertical(config, sidebarWidth);
            ImGui.sameLine();

            float lineX = ImGui.getCursorScreenPosX() - s(4.0f);
            float lineY1 = headerLineY;
            float lineY2 = ImGui.getCursorScreenPosY() + ImGui.getContentRegionAvail().y;

            ImGui.getWindowDrawList().addLine(lineX, lineY1, lineX, lineY2, ImGui.getColorU32(ImGuiCol.Separator), Math.max(1.0f, s(1.0f)));
            ComponentContent.render(config, false);
            ImGui.getWindowDrawList().addLine(headerLineX0, headerLineY, headerLineX1, headerLineY, ImGui.getColorU32(ImGuiCol.Separator), Math.max(1.0f, s(1.0f)));
        }

        if (draggingWindow) {
            if (!ImGui.isMouseDown(0)) {
                draggingWindow = false;
            }
        } else if (ImGui.isMouseClicked(0) && ImGui.isWindowHovered(ImGuiHoveredFlags.ChildWindows) && !ImGui.isAnyItemHovered()) {
            draggingWindow = true;
        }
        ImGui.end();
    }

    private static void drawWindowBackground() {
        float x = ImGui.getWindowPosX();
        float y = ImGui.getWindowPosY();
        float right = x + ImGui.getWindowWidth();
        float bottom = y + ImGui.getWindowHeight();
        ImGui.getBackgroundDrawList().addRectFilled(x + WINDOW_BG_INSET, y + WINDOW_BG_INSET, right - WINDOW_BG_INSET, bottom - WINDOW_BG_INSET, ImGuiTheme.windowBgColor(), s(12.0f));
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
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}