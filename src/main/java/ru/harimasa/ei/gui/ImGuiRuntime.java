package ru.harimasa.ei.gui;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public final class ImGuiRuntime {

    private static final ImGuiImplGl3 RENDERER = new ImGuiImplGl3();

    private static boolean initialized;
    private static boolean drawDataReady;

    public static final float UI_SCALE = 0.85f;

    private ImGuiRuntime() {}

    public static void init() {
        if (initialized) return;

        ImGui.createContext();
        ImGui.getIO().setIniFilename(null);

        ImGui.getStyle().scaleAllSizes(UI_SCALE);
        ImGui.getStyle().setSeparatorSize(Math.max(1.0f, ImGui.getStyle().getSeparatorSize()));
        ImGui.getStyle().setFontScaleMain(UI_SCALE);

        loadFont();

        RENDERER.init("#version 150");
        initialized = true;
    }

    private static void loadFont() {
        String path = "/assets/enchantments-indicator/fonts/Inter-Medium.ttf";
        try {
            var url = ImGuiRuntime.class.getResource(path);
            if (url == null) throw new IllegalStateException("Font not found: " + path);
            try (InputStream stream = url.openStream()) {
                byte[] fontData = stream.readAllBytes();
                if (fontData.length <= 100) throw new IllegalStateException("Font file is too small: " + fontData.length);
                ImGui.getIO().getFonts().addFontFromMemoryTTF(fontData, fontData.length, 16.0f, ImGui.getIO().getFonts().getGlyphRangesCyrillic());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void beginFrame(int width, int height) {
        if (!initialized) {
            init();
        }
        drawDataReady = false;

        Minecraft minecraft = Minecraft.getInstance();
        ImGui.getIO().setDisplaySize(width, height);
        ImGui.getIO().setDisplayFramebufferScale(minecraft.getWindow().getWidth() / (float) width, minecraft.getWindow().getHeight() / (float) height);

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