package ru.harimasa.ei.render;

import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;

/**
 * Separable Gaussian blur for the current Minecraft main render target.
 * The scene is downsampled to half resolution and blurred horizontally and
 * vertically, but the result is composited only inside the rounded ImGui panel.
 */
public final class LiquidGlassBlur {
    private static final Logger LOGGER = LoggerFactory.getLogger("enchantments-indicator");

    private static final float HORIZONTAL_RADIUS = 16.0f;
    private static final float VERTICAL_RADIUS = 8.0f;

    private static final String SHADER_DIRECTORY = "/assets/enchantments-indicator/shaders/blur/";

    private static int blurProgram;
    private static int copyProgram;
    private static int vertexArray;
    private static int linearClampSampler;

    private static int horizontalTexture;
    private static int verticalTexture;
    private static int horizontalFramebuffer;
    private static int verticalFramebuffer;
    private static int bufferWidth = -1;
    private static int bufferHeight = -1;

    private static int blurTextureLocation;
    private static int blurTexelSizeLocation;
    private static int blurDirectionLocation;
    private static int blurRadiusLocation;
    private static int copyTextureLocation;
    private static int copyRectMinLocation;
    private static int copyRectSizeLocation;
    private static int copyCornerRadiusLocation;

    private static boolean initialized;
    private static boolean failed;

    private LiquidGlassBlur() {}

    /**
     * Blurs the scene and composites it only inside the rounded ImGui window bounds.
     * GUI coordinates are converted to framebuffer pixels using the actual render scale.
     */
    public static void render(RenderTarget source, float guiX, float guiY, float guiWidth, float guiHeight,
                              float cornerRadius, float displayWidth, float displayHeight) {
        if (failed || source == null || source.width <= 0 || source.height <= 0 || displayWidth <= 0.0f || displayHeight <= 0.0f || guiWidth <= 0.0f || guiHeight <= 0.0f || !Float.isFinite(displayWidth) || !Float.isFinite(displayHeight) || !Float.isFinite(guiX) || !Float.isFinite(guiY) || !Float.isFinite(guiWidth) || !Float.isFinite(guiHeight) || !Float.isFinite(cornerRadius)) {
            return;
        }

        if (!(source.getColorTexture() instanceof GlTexture colorTexture)) {
            return;
        }

        float scaleX = source.width / displayWidth;
        float scaleY = source.height / displayHeight;

        int rectLeft = clamp((int) Math.floor(guiX * scaleX), 0, source.width);
        int rectRight = clamp((int) Math.ceil((guiX + guiWidth) * scaleX), 0, source.width);
        // ImGui uses a top-left origin while OpenGL's framebuffer origin is bottom-left.
        int rectBottom = clamp((int) Math.floor(source.height - (guiY + guiHeight) * scaleY), 0, source.height);
        int rectTop = clamp((int) Math.ceil(source.height - guiY * scaleY), 0, source.height);
        int rectWidth = rectRight - rectLeft;
        int rectHeight = rectTop - rectBottom;
        if (rectWidth <= 0 || rectHeight <= 0) return;

        float framebufferCornerRadius = Math.max(0.0f, cornerRadius * Math.min(scaleX, scaleY));

        try (GlState state = new GlState()) {
            try {
                ensurePrograms();
                ensureBuffers(source.width, source.height);
                int mainFramebuffer = colorTexture.getFbo(((GlDevice) RenderSystem.getDevice()).directStateAccess(), null);
                renderBlur(colorTexture.glId(), mainFramebuffer, source.width, source.height,
                        rectLeft, rectBottom, rectWidth, rectHeight, framebufferCornerRadius);
            } catch (RuntimeException | LinkageError exception) {
                failed = true;
                destroyResources();
                LOGGER.error("Liquid Glass Gaussian blur could not be initialized; disabling blur", exception);
            }
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static void ensurePrograms() {
        if (initialized) return;

        int vertexShader = compileShader(GL20C.GL_VERTEX_SHADER, readShader("fullscreen.vsh"));
        int blurFragment = 0;
        int copyFragment = 0;
        try {
            blurFragment = compileShader(GL20C.GL_FRAGMENT_SHADER, readShader("gaussian_blur.fsh"));
            copyFragment = compileShader(GL20C.GL_FRAGMENT_SHADER, readShader("copy.fsh"));
            blurProgram = linkProgram(vertexShader, blurFragment);
            copyProgram = linkProgram(vertexShader, copyFragment);
            vertexArray = GL30C.glGenVertexArrays();
            linearClampSampler = GL33C.glGenSamplers();
            GL33C.glSamplerParameteri(linearClampSampler, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);
            GL33C.glSamplerParameteri(linearClampSampler, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
            GL33C.glSamplerParameteri(linearClampSampler, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
            GL33C.glSamplerParameteri(linearClampSampler, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);

            blurTextureLocation = GL20C.glGetUniformLocation(blurProgram, "uTexture");
            blurTexelSizeLocation = GL20C.glGetUniformLocation(blurProgram, "uTexelSize");
            blurDirectionLocation = GL20C.glGetUniformLocation(blurProgram, "uDirection");
            blurRadiusLocation = GL20C.glGetUniformLocation(blurProgram, "uRadius");
            copyTextureLocation = GL20C.glGetUniformLocation(copyProgram, "uTexture");
            copyRectMinLocation = GL20C.glGetUniformLocation(copyProgram, "uRectMin");
            copyRectSizeLocation = GL20C.glGetUniformLocation(copyProgram, "uRectSize");
            copyCornerRadiusLocation = GL20C.glGetUniformLocation(copyProgram, "uCornerRadius");
            initialized = true;
        } finally {
            GL20C.glDeleteShader(vertexShader);
            if (blurFragment != 0) GL20C.glDeleteShader(blurFragment);
            if (copyFragment != 0) GL20C.glDeleteShader(copyFragment);
        }
    }

    private static String readShader(String fileName) {
        String resourcePath = SHADER_DIRECTORY + fileName;
        try (java.io.InputStream input = LiquidGlassBlur.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Missing Liquid Glass shader resource: " + resourcePath);
            }
            return new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Failed to read Liquid Glass shader resource: " + resourcePath, exception);
        }
    }

    private static int compileShader(int type, String source) {
        int shader = GL20C.glCreateShader(type);
        if (shader == 0) throw new IllegalStateException("OpenGL returned shader id 0");

        GL20C.glShaderSource(shader, source);
        GL20C.glCompileShader(shader);
        if (GL20C.glGetShaderi(shader, GL20C.GL_COMPILE_STATUS) == GL11C.GL_FALSE) {
            String log = GL20C.glGetShaderInfoLog(shader);
            GL20C.glDeleteShader(shader);
            throw new IllegalStateException("Liquid Glass shader compilation failed: " + log);
        }
        return shader;
    }

    private static int linkProgram(int vertexShader, int fragmentShader) {
        int program = GL20C.glCreateProgram();
        if (program == 0) throw new IllegalStateException("OpenGL returned program id 0");

        GL20C.glAttachShader(program, vertexShader);
        GL20C.glAttachShader(program, fragmentShader);
        GL20C.glLinkProgram(program);
        if (GL20C.glGetProgrami(program, GL20C.GL_LINK_STATUS) == GL11C.GL_FALSE) {
            String log = GL20C.glGetProgramInfoLog(program);
            GL20C.glDeleteProgram(program);
            throw new IllegalStateException("Liquid Glass shader linking failed: " + log);
        }
        return program;
    }

    private static void ensureBuffers(int width, int height) {
        int targetWidth = Math.max(1, (width + 1) / 2);
        int targetHeight = Math.max(1, (height + 1) / 2);
        if (bufferWidth == targetWidth && bufferHeight == targetHeight) return;

        destroyBuffers();

        horizontalTexture = createTexture(targetWidth, targetHeight);
        verticalTexture = createTexture(targetWidth, targetHeight);
        horizontalFramebuffer = createFramebuffer(horizontalTexture);
        verticalFramebuffer = createFramebuffer(verticalTexture);
        bufferWidth = targetWidth;
        bufferHeight = targetHeight;
    }

    private static int createTexture(int width, int height) {
        int texture = GL11C.glGenTextures();
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, texture);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);
        GL11C.glTexImage2D(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_RGBA8, width, height, 0, GL11C.GL_RGBA, GL11C.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        return texture;
    }

    private static int createFramebuffer(int texture) {
        int framebuffer = GL30C.glGenFramebuffers();
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, framebuffer);
        GL30C.glFramebufferTexture2D(GL30C.GL_FRAMEBUFFER, GL30C.GL_COLOR_ATTACHMENT0,
                GL11C.GL_TEXTURE_2D, texture, 0);

        int status = GL30C.glCheckFramebufferStatus(GL30C.GL_FRAMEBUFFER);
        if (status != GL30C.GL_FRAMEBUFFER_COMPLETE) {
            GL30C.glDeleteFramebuffers(framebuffer);
            throw new IllegalStateException("Liquid Glass framebuffer is incomplete: 0x" + Integer.toHexString(status));
        }
        return framebuffer;
    }

    private static void renderBlur(int sourceTexture, int mainFramebuffer, int width, int height, int rectX, int rectY, int rectWidth, int rectHeight, float cornerRadius) {
        GL11C.glDisable(GL11C.GL_BLEND);
        GL11C.glDisable(GL11C.GL_DEPTH_TEST);
        GL11C.glDisable(GL11C.GL_CULL_FACE);
        GL11C.glDisable(GL11C.GL_SCISSOR_TEST);
        GL11C.glDisable(GL11C.GL_STENCIL_TEST);

        GL13C.glActiveTexture(GL13C.GL_TEXTURE0);
        GL33C.glBindSampler(0, linearClampSampler);
        GL30C.glBindVertexArray(vertexArray);

        // Pass 1: downsample and blur horizontally.
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, horizontalFramebuffer);
        GL11C.glViewport(0, 0, bufferWidth, bufferHeight);
        GL20C.glUseProgram(blurProgram);
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, sourceTexture);
        GL20C.glUniform1i(blurTextureLocation, 0);
        GL20C.glUniform2f(blurTexelSizeLocation, 1.0f / width, 1.0f / height);
        GL20C.glUniform2f(blurDirectionLocation, 1.0f, 0.0f);
        GL20C.glUniform1f(blurRadiusLocation, HORIZONTAL_RADIUS);
        GL11C.glDrawArrays(GL11C.GL_TRIANGLES, 0, 3);

        // Pass 2: blur vertically at half resolution.
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, verticalFramebuffer);
        GL11C.glViewport(0, 0, bufferWidth, bufferHeight);
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, horizontalTexture);
        GL20C.glUniform2f(blurTexelSizeLocation, 1.0f / bufferWidth, 1.0f / bufferHeight);
        GL20C.glUniform2f(blurDirectionLocation, 0.0f, 1.0f);
        GL20C.glUniform1f(blurRadiusLocation, VERTICAL_RADIUS);
        GL11C.glDrawArrays(GL11C.GL_TRIANGLES, 0, 3);

        // Composite only inside the ImGui window. Scissor limits fragment work to its
        // bounding box; the copy shader discards pixels outside its rounded corners.
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, mainFramebuffer);
        GL11C.glViewport(0, 0, width, height);
        GL11C.glScissor(rectX, rectY, rectWidth, rectHeight);
        GL11C.glEnable(GL11C.GL_SCISSOR_TEST);
        GL20C.glUseProgram(copyProgram);
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, verticalTexture);
        GL20C.glUniform1i(copyTextureLocation, 0);
        GL20C.glUniform2f(copyRectMinLocation, rectX, rectY);
        GL20C.glUniform2f(copyRectSizeLocation, rectWidth, rectHeight);
        GL20C.glUniform1f(copyCornerRadiusLocation, cornerRadius);
        GL11C.glDrawArrays(GL11C.GL_TRIANGLES, 0, 3);
    }

    /** Releases all resources while the OpenGL context is still alive. */
    public static void shutdown() {
        destroyResources();
        failed = false;
    }

    private static void destroyResources() {
        destroyBuffers();
        if (blurProgram != 0) GL20C.glDeleteProgram(blurProgram);
        if (copyProgram != 0) GL20C.glDeleteProgram(copyProgram);
        if (vertexArray != 0) GL30C.glDeleteVertexArrays(vertexArray);
        if (linearClampSampler != 0) GL33C.glDeleteSamplers(linearClampSampler);
        blurProgram = 0;
        copyProgram = 0;
        vertexArray = 0;
        linearClampSampler = 0;
        initialized = false;
    }

    private static void destroyBuffers() {
        if (horizontalFramebuffer != 0) GL30C.glDeleteFramebuffers(horizontalFramebuffer);
        if (verticalFramebuffer != 0) GL30C.glDeleteFramebuffers(verticalFramebuffer);
        if (horizontalTexture != 0) GL11C.glDeleteTextures(horizontalTexture);
        if (verticalTexture != 0) GL11C.glDeleteTextures(verticalTexture);
        horizontalFramebuffer = 0;
        verticalFramebuffer = 0;
        horizontalTexture = 0;
        verticalTexture = 0;
        bufferWidth = -1;
        bufferHeight = -1;
    }

    private static final class GlState implements AutoCloseable {
        private final int drawFramebuffer;
        private final int readFramebuffer;
        private final int activeTexture;
        private final int texture0Binding;
        private final int sampler0Binding;
        private final int program;
        private final int vertexArray;
        private final boolean blend;
        private final boolean depth;
        private final boolean cull;
        private final boolean scissor;
        private final boolean stencil;
        private final MemoryStack stack;
        private final IntBuffer viewport;
        private final IntBuffer scissorBox;

        private GlState() {
            drawFramebuffer = GL11C.glGetInteger(GL30C.GL_DRAW_FRAMEBUFFER_BINDING);
            readFramebuffer = GL11C.glGetInteger(GL30C.GL_READ_FRAMEBUFFER_BINDING);
            activeTexture = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
            program = GL11C.glGetInteger(GL20C.GL_CURRENT_PROGRAM);
            vertexArray = GL11C.glGetInteger(GL30C.GL_VERTEX_ARRAY_BINDING);
            blend = GL11C.glIsEnabled(GL11C.GL_BLEND);
            depth = GL11C.glIsEnabled(GL11C.GL_DEPTH_TEST);
            cull = GL11C.glIsEnabled(GL11C.GL_CULL_FACE);
            scissor = GL11C.glIsEnabled(GL11C.GL_SCISSOR_TEST);
            stencil = GL11C.glIsEnabled(GL11C.GL_STENCIL_TEST);

            stack = MemoryStack.stackPush();
            viewport = stack.mallocInt(4);
            scissorBox = stack.mallocInt(4);
            GL11C.glGetIntegerv(GL11C.GL_VIEWPORT, viewport);
            GL11C.glGetIntegerv(GL11C.GL_SCISSOR_BOX, scissorBox);

            GL13C.glActiveTexture(GL13C.GL_TEXTURE0);
            texture0Binding = GL11C.glGetInteger(GL11C.GL_TEXTURE_BINDING_2D);
            sampler0Binding = GL11C.glGetInteger(GL33C.GL_SAMPLER_BINDING);
        }

        @Override
        public void close() {
            try {
                GL30C.glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
                GL30C.glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, readFramebuffer);
                GL11C.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
                GL11C.glScissor(scissorBox.get(0), scissorBox.get(1), scissorBox.get(2), scissorBox.get(3));

                GL20C.glUseProgram(program);
                GL30C.glBindVertexArray(vertexArray);

                GL13C.glActiveTexture(GL13C.GL_TEXTURE0);
                GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, texture0Binding);
                GL33C.glBindSampler(0, sampler0Binding);
                GL13C.glActiveTexture(activeTexture);

                setEnabled(GL11C.GL_BLEND, blend);
                setEnabled(GL11C.GL_DEPTH_TEST, depth);
                setEnabled(GL11C.GL_CULL_FACE, cull);
                setEnabled(GL11C.GL_SCISSOR_TEST, scissor);
                setEnabled(GL11C.GL_STENCIL_TEST, stencil);
            } finally {
                stack.close();
            }
        }

        private static void setEnabled(int capability, boolean enabled) {
            if (enabled) GL11C.glEnable(capability);
            else GL11C.glDisable(capability);
        }
    }
}
