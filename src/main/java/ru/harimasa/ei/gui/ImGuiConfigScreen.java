package ru.harimasa.ei.gui;

import com.mojang.blaze3d.platform.InputConstants;
import imgui.ImGui;
import imgui.flag.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.gui.menu.component.ComponentContent;
import ru.harimasa.ei.gui.menu.component.ComponentHeader;
import ru.harimasa.ei.gui.menu.component.ComponentSidebar;
import ru.harimasa.ei.gui.menu.util.ImGuiTheme;

import static ru.harimasa.ei.gui.menu.util.ImGuiUtil.s;

public final class ImGuiConfigScreen extends Screen {

    private final EIConfig config = new EIConfig();

    public ImGuiConfigScreen() {
        super(Component.literal("Enchantments Indicator"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0x88000000);
        ImGuiRuntime.beginFrame(width, height);
        ImGui.getIO().addMousePosEvent((float) mouseX, (float) mouseY);
        ImGuiTheme.pushTheme();
        drawMenu();
        ImGui.popStyleColor(ImGuiTheme.THEME_COLOR_COUNT);
        ImGui.popStyleVar(7);
        ImGuiRuntime.endFrame();
    }

    private void drawMenu() {
        float windowWidth = Math.min(s(610), width - s(32));
        float windowHeight = Math.min(s(342), height - s(32));
        ImGui.setNextWindowPos((width - windowWidth) * 0.5f, (height - windowHeight) * 0.5f, imgui.flag.ImGuiCond.Once);
        ImGui.setNextWindowSize(windowWidth, windowHeight, imgui.flag.ImGuiCond.Always);

        int flags = imgui.flag.ImGuiWindowFlags.NoResize | imgui.flag.ImGuiWindowFlags.NoCollapse |
                imgui.flag.ImGuiWindowFlags.NoTitleBar | imgui.flag.ImGuiWindowFlags.NoScrollbar;

        if (ImGui.begin("##enchantments_indicator", flags)) {
            ComponentHeader.render();
            ImGui.separator();
            ComponentSidebar.render(config);
            ImGui.sameLine();

            // Draw vertical separator line
            float lineX = ImGui.getCursorScreenPosX() - s(4);
            float lineY1 = ImGui.getCursorScreenPosY();
            float lineY2 = lineY1 + ImGui.getContentRegionAvail().y;
            ImGui.getWindowDrawList().addLine(lineX, lineY1, lineX, lineY2, ImGui.getColorU32(imgui.flag.ImGuiCol.Separator), 1.0f);

            ComponentContent.render(config);
        }
        ImGui.end();
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        ImGui.getIO().addMousePosEvent((float) event.x(), (float) event.y());
        ImGui.getIO().addMouseButtonEvent(event.button(), true);
        return true;
    }
    @Override public boolean mouseReleased(MouseButtonEvent event) {
        ImGui.getIO().addMousePosEvent((float) event.x(), (float) event.y());
        ImGui.getIO().addMouseButtonEvent(event.button(), false);
        return true;
    }
    @Override public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        ImGui.getIO().addMousePosEvent((float) event.x(), (float) event.y());
        return true;
    }
    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        ImGui.getIO().addMousePosEvent((float) mouseX, (float) mouseY);
        ImGui.getIO().addMouseWheelEvent((float) horizontal, (float) vertical);
        return true;
    }
    @Override public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_ESCAPE) { onClose(); return true; }
        return super.keyPressed(event);
    }
    @Override public void onClose() { Minecraft.getInstance().setScreen(null); }
    @Override public boolean isPauseScreen() { return false; }
}