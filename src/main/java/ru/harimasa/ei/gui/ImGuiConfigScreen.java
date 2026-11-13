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
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.WindowPosition;
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

    private final EIConfig config = ConfigManager.get();

    private int lastScreenWidth = -1;
    private int lastScreenHeight = -1;

    private float windowX = Float.NaN;
    private float windowY = Float.NaN;
    private boolean draggingWindow;

    public ImGuiConfigScreen() {
        super(Component.translatable("ei.menu.title"));

        WindowPosition savedPosition = config.general.window;
        if (savedPosition.saved) {
            windowX = savedPosition.x;
            windowY = savedPosition.y;
        }
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        boolean glass = config.general.liquidGlass;
        ImGuiRuntime.setBackgroundBlurEnabled(glass);

        ImGuiRuntime.beginFrame(width, height, mouseX, mouseY);
        ImGuiTheme.pushTheme();
        ImGui.getBackgroundDrawList().addRectFilled(0.0f, 0.0f, width, height, glass ? 0x00000000 : 0x88000000);        drawMenu();
        ImGui.popStyleColor(ImGuiTheme.THEME_COLOR_COUNT);
        ImGui.popStyleVar(7);
        ImGuiRuntime.endFrame();
    }

    private void drawMenu() {
        float windowWidth = Math.min(s(MAX_WINDOW_WIDTH), Math.max(s(MIN_WINDOW_WIDTH), width - s(32.0f)));
        float windowHeight = Math.min(s(MAX_WINDOW_HEIGHT), Math.max(s(MIN_WINDOW_HEIGHT), height - s(32.0f)));
        windowWidth = Math.min(windowWidth, Math.max(1.0f, width - s(8.0f)));
        windowHeight = Math.min(windowHeight, Math.max(1.0f, height - s(8.0f)));

        boolean firstFrame = lastScreenWidth < 0 || lastScreenHeight < 0;
        boolean screenChanged = width != lastScreenWidth || height != lastScreenHeight;

        if (Float.isNaN(windowX) || Float.isNaN(windowY) || (screenChanged && !firstFrame)) {
            windowX = (width - windowWidth) * 0.5f;
            windowY = (height - windowHeight) * 0.5f;
        }

        lastScreenWidth = width;
        lastScreenHeight = height;

        boolean dragJustEnded = false;
        if (draggingWindow) {
            if (!ImGui.isMouseDown(0)) {
                draggingWindow = false;
                dragJustEnded = true;
            } else {
                windowX += ImGui.getIO().getMouseDeltaX();
                windowY += ImGui.getIO().getMouseDeltaY();
            }
        }
        windowX = Math.max(0.0f, Math.min(windowX, Math.max(0.0f, width - windowWidth)));
        windowY = Math.max(0.0f, Math.min(windowY, Math.max(0.0f, height - windowHeight)));

        if (dragJustEnded) {
            saveWindowPosition();
        }

        ImGui.setNextWindowPos(windowX, windowY, ImGuiCond.Always);
        ImGui.setNextWindowSize(windowWidth, windowHeight, ImGuiCond.Always);

        int flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoMove;
        boolean windowVisible = ImGui.begin("##enchantments_indicator", flags);

        ImGuiRuntime.setBlurRegion(ImGui.getWindowPosX() + WINDOW_BG_INSET, ImGui.getWindowPosY() + WINDOW_BG_INSET, ImGui.getWindowWidth() - WINDOW_BG_INSET * 2.0f, ImGui.getWindowHeight() - WINDOW_BG_INSET * 2.0f, s(12.0f));

        if (!windowVisible) {
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

    private void drawWindowBackground() {
        float x = ImGui.getWindowPosX() + WINDOW_BG_INSET;
        float y = ImGui.getWindowPosY() + WINDOW_BG_INSET;
        float right = ImGui.getWindowPosX() + ImGui.getWindowWidth() - WINDOW_BG_INSET;
        float bottom = ImGui.getWindowPosY() + ImGui.getWindowHeight() - WINDOW_BG_INSET;
        float rounding = s(12.0f);

        var drawList = ImGui.getBackgroundDrawList();
        if (config.general.liquidGlass) {
            drawList.addRectFilled(x, y, right, bottom, ImGui.getColorU32(0.06f, 0.065f, 0.08f, 0.38f), rounding);
            drawList.addRect(x, y, right, bottom, ImGui.getColorU32(1.0f, 1.0f, 1.0f, 0.8f), rounding, 0, 1.0f);
        } else {
            drawList.addRectFilled(x, y, right, bottom, ImGuiTheme.windowBgColor(), rounding);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (ImGui.getCurrentContext().isValidPtr()) {
            ImGui.getIO().addMousePosEvent((float) event.x(), (float) event.y());
            ImGui.getIO().addMouseButtonEvent(event.button(), true);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (ImGui.getCurrentContext().isValidPtr()) {
            ImGui.getIO().addMousePosEvent((float) event.x(), (float) event.y());
            ImGui.getIO().addMouseButtonEvent(event.button(), false);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (ImGui.getCurrentContext().isValidPtr()) {
            ImGui.getIO().addMousePosEvent((float) event.x(), (float) event.y());
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (ImGui.getCurrentContext().isValidPtr()) {
            ImGui.getIO().addMousePosEvent((float) mouseX, (float) mouseY);
            ImGui.getIO().addMouseWheelEvent((float) horizontal, (float) vertical);
        }
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
    public void removed() {
        saveWindowPosition();
        ConfigManager.save();
        super.removed();
    }

    private void saveWindowPosition() {
        if (!Float.isFinite(windowX) || !Float.isFinite(windowY)) return;

        WindowPosition position = config.general.window;
        if (position.saved && position.x == windowX && position.y == windowY) return;

        position.saved = true;
        position.x = windowX;
        position.y = windowY;
        ConfigManager.markDirty();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}