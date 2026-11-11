package ru.harimasa.ei.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import org.slf4j.LoggerFactory;

public final class EIGlintShaders {
    private static final Identifier PIPELINE_ID = Identifier.fromNamespaceAndPath("enchantments-indicator", "pipeline/glint_colored");
    private static final Identifier SHADER = Identifier.fromNamespaceAndPath("enchantments-indicator", "core/glint_colored");

    private static final RenderPipeline PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
                    .withLocation(PIPELINE_ID)
                    .withVertexShader(SHADER)
                    .withFragmentShader(SHADER)
                    .withSampler("Sampler0")
                    .withDepthWrite(false)
                    .withCull(false)
                    .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
                    .withBlend(BlendFunction.GLINT)
                    .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
                    .build());

    public static final RenderType ARMOR_GLINT = RenderType.create("enchantments_indicator_glint_colored", RenderSetup.builder(PIPELINE)
                    .withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ARMOR)
                    .setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup());

    private EIGlintShaders() {}

    public static void ensureInitialized() {
        LoggerFactory.getLogger("enchantments_indicator").debug("[EI] the pipeline is registered before the shaders");
    }
}