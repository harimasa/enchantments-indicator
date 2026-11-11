package ru.harimasa.ei.gui;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.gl3.ImGuiImplGl3;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public final class ImGuiRuntime {
    private static final float MIN_UI_SCALE = 0.50f;
    private static final float MAX_UI_SCALE = 2.00f;
    private static final float BASE_FONT_SIZE = 16.0f;

    private static final ImGuiImplGl3 RENDERER = new ImGuiImplGl3();

    private static boolean initialized;
    private static boolean drawDataReady;

    public static float UI_SCALE = 1.00f;

    private static float appliedScale = 1.00f;
    private static float pendingScale = Float.NaN;

    private static BaseStyle baseStyle;
    private static ImFont boldFont;

    private ImGuiRuntime() {}

    public static void setUiScale(float scale) {
        if (Float.isNaN(scale)) return;
        pendingScale = Math.max(MIN_UI_SCALE, Math.min(MAX_UI_SCALE, scale));
    }

    public static void init() {
        if (initialized) return;

        ImGui.createContext();
        ImGui.getIO().setIniFilename(null);

        loadFont();
        applyScale(UI_SCALE);

        RENDERER.init("#version 150");
        initialized = true;
    }

    private static void loadFont() {
        ImFontAtlas fonts = ImGui.getIO().getFonts();
        short[] glyphRanges = fonts.getGlyphRangesCyrillic();
        addFont(fonts, "/assets/enchantments-indicator/fonts/Inter-Medium.ttf", glyphRanges);
        boldFont = addFont(fonts, "/assets/enchantments-indicator/fonts/Inter-Bold.ttf", glyphRanges);
    }

    private static ImFont addFont(ImFontAtlas fonts, String path, short[] glyphRanges) {
        try {
            var url = ImGuiRuntime.class.getResource(path);
            if (url == null) throw new IllegalStateException("Font not found: " + path);
            try (InputStream stream = url.openStream()) {
                byte[] fontData = stream.readAllBytes();
                if (fontData.length <= 100) throw new IllegalStateException("Font file is too small: " + fontData.length);
                return fonts.addFontFromMemoryTTF(fontData, fontData.length, BASE_FONT_SIZE, glyphRanges);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static ImFont boldFont() {
        return boldFont;
    }

    private static void applyPendingScale() {
        if (Float.isNaN(pendingScale)) return;
        float next = pendingScale;
        pendingScale = Float.NaN;
        if (Math.abs(next - appliedScale) < 0.0001f) {
            UI_SCALE = next;
            return;
        }
        applyScale(next);
    }

    private static void applyScale(float scale) {
        float clamped = Math.max(MIN_UI_SCALE, Math.min(MAX_UI_SCALE, scale));
        ImGuiStyle style = ImGui.getStyle();

        if (baseStyle == null) baseStyle = new BaseStyle(style);
        baseStyle.applyTo(style, clamped);
        appliedScale = clamped;
        UI_SCALE = clamped;
        style.setFontScaleMain(clamped);
    }

    private static final class BaseStyle {
        private final float windowPaddingX, windowPaddingY;
        private final float windowRounding;
        private final float windowBorderSize;
        private final float windowMinSizeX, windowMinSizeY;
        private final float childRounding;
        private final float childBorderSize;
        private final float popupRounding;
        private final float popupBorderSize;
        private final float framePaddingX, framePaddingY;
        private final float frameRounding;
        private final float frameBorderSize;
        private final float itemSpacingX, itemSpacingY;
        private final float itemInnerSpacingX, itemInnerSpacingY;
        private final float cellPaddingX, cellPaddingY;
        private final float touchExtraPaddingX, touchExtraPaddingY;
        private final float indentSpacing;
        private final float columnsMinSpacing;
        private final float scrollbarSize;
        private final float scrollbarRounding;
        private final float grabMinSize;
        private final float grabRounding;
        private final float logSliderDeadzone;
        private final float imageRounding;
        private final float imageBorderSize;
        private final float tabRounding;
        private final float tabBorderSize;
        private final float colorMarkerSize;
        private final float treeLinesSize;
        private final float treeLinesRounding;
        private final float separatorSize;
        private final float separatorTextBorderSize;
        private final float separatorTextPaddingX, separatorTextPaddingY;
        private final float displayWindowPaddingX, displayWindowPaddingY;
        private final float displaySafeAreaPaddingX, displaySafeAreaPaddingY;
        private final float mouseCursorScale;

        private BaseStyle(ImGuiStyle style) {
            windowPaddingX = style.getWindowPaddingX();
            windowPaddingY = style.getWindowPaddingY();
            windowRounding = style.getWindowRounding();
            windowBorderSize = style.getWindowBorderSize();
            windowMinSizeX = style.getWindowMinSizeX();
            windowMinSizeY = style.getWindowMinSizeY();
            childRounding = style.getChildRounding();
            childBorderSize = style.getChildBorderSize();
            popupRounding = style.getPopupRounding();
            popupBorderSize = style.getPopupBorderSize();
            framePaddingX = style.getFramePaddingX();
            framePaddingY = style.getFramePaddingY();
            frameRounding = style.getFrameRounding();
            frameBorderSize = style.getFrameBorderSize();
            itemSpacingX = style.getItemSpacingX();
            itemSpacingY = style.getItemSpacingY();
            itemInnerSpacingX = style.getItemInnerSpacingX();
            itemInnerSpacingY = style.getItemInnerSpacingY();
            cellPaddingX = style.getCellPaddingX();
            cellPaddingY = style.getCellPaddingY();
            touchExtraPaddingX = style.getTouchExtraPaddingX();
            touchExtraPaddingY = style.getTouchExtraPaddingY();
            indentSpacing = style.getIndentSpacing();
            columnsMinSpacing = style.getColumnsMinSpacing();
            scrollbarSize = style.getScrollbarSize();
            scrollbarRounding = style.getScrollbarRounding();
            grabMinSize = style.getGrabMinSize();
            grabRounding = style.getGrabRounding();
            logSliderDeadzone = style.getLogSliderDeadzone();
            imageRounding = style.getImageRounding();
            imageBorderSize = style.getImageBorderSize();
            tabRounding = style.getTabRounding();
            tabBorderSize = style.getTabBorderSize();
            colorMarkerSize = style.getColorMarkerSize();
            treeLinesSize = style.getTreeLinesSize();
            treeLinesRounding = style.getTreeLinesRounding();
            separatorSize = style.getSeparatorSize();
            separatorTextBorderSize = style.getSeparatorTextBorderSize();
            separatorTextPaddingX = style.getSeparatorTextPaddingX();
            separatorTextPaddingY = style.getSeparatorTextPaddingY();
            displayWindowPaddingX = style.getDisplayWindowPaddingX();
            displayWindowPaddingY = style.getDisplayWindowPaddingY();
            displaySafeAreaPaddingX = style.getDisplaySafeAreaPaddingX();
            displaySafeAreaPaddingY = style.getDisplaySafeAreaPaddingY();
            mouseCursorScale = style.getMouseCursorScale();
        }

        private void applyTo(ImGuiStyle style, float scale) {
            style.setWindowPadding(windowPaddingX * scale, windowPaddingY * scale);
            style.setWindowRounding(windowRounding * scale);
            style.setWindowBorderSize(windowBorderSize * scale);
            style.setWindowMinSize(windowMinSizeX * scale, windowMinSizeY * scale);
            style.setChildRounding(childRounding * scale);
            style.setChildBorderSize(childBorderSize * scale);
            style.setPopupRounding(popupRounding * scale);
            style.setPopupBorderSize(popupBorderSize * scale);
            style.setFramePadding(framePaddingX * scale, framePaddingY * scale);
            style.setFrameRounding(frameRounding * scale);
            style.setFrameBorderSize(frameBorderSize * scale);
            style.setItemSpacing(itemSpacingX * scale, itemSpacingY * scale);
            style.setItemInnerSpacing(itemInnerSpacingX * scale, itemInnerSpacingY * scale);
            style.setCellPadding(cellPaddingX * scale, cellPaddingY * scale);
            style.setTouchExtraPadding(touchExtraPaddingX * scale, touchExtraPaddingY * scale);
            style.setIndentSpacing(indentSpacing * scale);
            style.setColumnsMinSpacing(columnsMinSpacing * scale);
            style.setScrollbarSize(scrollbarSize * scale);
            style.setScrollbarRounding(scrollbarRounding * scale);
            style.setGrabMinSize(grabMinSize * scale);
            style.setGrabRounding(grabRounding * scale);
            style.setLogSliderDeadzone(logSliderDeadzone * scale);
            style.setImageRounding(imageRounding * scale);
            style.setImageBorderSize(imageBorderSize * scale);
            style.setTabRounding(tabRounding * scale);
            style.setTabBorderSize(tabBorderSize * scale);
            style.setColorMarkerSize(colorMarkerSize * scale);
            style.setTreeLinesSize(treeLinesSize * scale);
            style.setTreeLinesRounding(treeLinesRounding * scale);
            style.setSeparatorSize(Math.max(1.0f, separatorSize * scale));
            style.setSeparatorTextBorderSize(separatorTextBorderSize * scale);
            style.setSeparatorTextPadding(separatorTextPaddingX * scale, separatorTextPaddingY * scale);
            style.setDisplayWindowPadding(displayWindowPaddingX * scale, displayWindowPaddingY * scale);
            style.setDisplaySafeAreaPadding(displaySafeAreaPaddingX * scale, displaySafeAreaPaddingY * scale);
            style.setMouseCursorScale(mouseCursorScale * scale);
        }
    }

    public static void beginFrame(int width, int height, float mouseX, float mouseY) {
        if (!initialized) {
            init();
        }
        applyPendingScale();
        drawDataReady = false;
        Minecraft minecraft = Minecraft.getInstance();
        ImGuiIO io = ImGui.getIO();
        if (width > 0 && height > 0) {
            io.setDisplaySize(width, height);
            io.setDisplayFramebufferScale(minecraft.getWindow().getWidth() / (float) width, minecraft.getWindow().getHeight() / (float) height);
        }
        io.addMousePosEvent(mouseX, mouseY);

        RENDERER.newFrame();
        ImGui.newFrame();
    }

    public static void endFrame() {
        if (!initialized) return;

        ImGui.render();
        drawDataReady = true;
    }

    public static void renderDrawData() {
        if (!initialized || !drawDataReady) return;

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget framebuffer = minecraft.getMainRenderTarget();
        assert framebuffer.getColorTexture() != null;
        int framebufferId = ((GlTexture) framebuffer.getColorTexture()).getFbo(((GlDevice) RenderSystem.getDevice()).directStateAccess(), null);

        GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, framebufferId);
        GL11C.glViewport(0, 0, framebuffer.width, framebuffer.height);

        try {
            RENDERER.renderDrawData(ImGui.getDrawData());
        } finally {
            GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, 0);
            drawDataReady = false;
        }
    }

    public static void shutdown() {
        if (!initialized) return;

        RENDERER.shutdown();
        ImGui.destroyContext();

        initialized = false;
        drawDataReady = false;
    }
}