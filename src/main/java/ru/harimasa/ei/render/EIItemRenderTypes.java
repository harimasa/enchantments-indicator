package ru.harimasa.ei.render;

import java.util.List;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.slf4j.LoggerFactory;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.enums.RenderMode;

public final class EIItemRenderTypes {
    private static final RenderType ITEM_SHEET = createItemSheet();
    private static final RenderType BLOCK_CUTOUT = createBlockCutout();
    private static final RenderType BLOCK_TRANSLUCENT = createBlockTranslucent();

    private static final RenderType ITEM_GLINT = createGlint(false);
    private static final RenderType ITEM_GLINT_TRANSLUCENT = createGlint(true);

    private EIItemRenderTypes() {}

    private static RenderType createItemSheet() {
        return RenderType.create("enchantments_indicator_item_sheet", RenderSetup.builder(RenderPipelines.ITEM_ENTITY_TRANSLUCENT_CULL)
                        .withTexture("Sampler0", TextureAtlas.LOCATION_ITEMS)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .sortOnUpload()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup());
    }

    private static RenderType createBlockCutout() {
        return RenderType.create("enchantments_indicator_block_cutout", RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT)
                        .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup());
    }

    private static RenderType createBlockTranslucent() {
        return RenderType.create("enchantments_indicator_block_translucent", RenderSetup.builder(RenderPipelines.ITEM_ENTITY_TRANSLUCENT_CULL)
                .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup());
    }

    private static RenderType createGlint(boolean translucent) {
        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder(RenderPipelines.GLINT)
                .withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ITEM)
                .setTextureTransform(TextureTransform.GLINT_TEXTURING);
        if (translucent) builder.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET);
        return RenderType.create(translucent ? "enchantments_indicator_item_glint_translucent" : "enchantments_indicator_item_glint", builder.createRenderSetup());
    }

    public static RenderType overrideBase(RenderType original, ItemStack stack, ItemDisplayContext context) {
        EIConfig config = ConfigManager.get();
        if (!config.general.modEnabled || !config.inventory.enabled) return original;
        if (!isSupportedContext(context)) return original;
        if (!EnchantmentTint.hasEnchantment(stack, config.colors.enchantment)) return original;

        RenderType replacement = baseFor(original);
        return replacement != null ? replacement : original;
    }

    public record IdentityKey(RenderMode mode, int argb, float intensity) {}

    public static IdentityKey identityKey() {
        EIConfig config = ConfigManager.get();
        return new IdentityKey(config.inventory.renderMode, EnchantmentTint.color(config.colors.enchantmentColor, false), Math.clamp(config.colors.intensity, 0.0f, 2.0f));
    }

    public static RenderType glintFor(RenderType base, RenderType original) {
        if (!isBase(base)) return original;

        EIConfig config = ConfigManager.get();
        if (!config.general.modEnabled || !config.inventory.enabled) return original;
        if (config.inventory.renderMode != RenderMode.GLINT) return original;
        return isTranslucentBase(base) ? ITEM_GLINT_TRANSLUCENT : ITEM_GLINT;
    }

    private static boolean isSupportedContext(ItemDisplayContext context) {
        return context == ItemDisplayContext.GUI || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    private static RenderType baseFor(RenderType original) {
        if (original == Sheets.translucentItemSheet()) return ITEM_SHEET;
        if (original == Sheets.cutoutBlockSheet()) return BLOCK_CUTOUT;
        if (original == Sheets.translucentBlockItemSheet()) return BLOCK_TRANSLUCENT;
        return null;
    }

    public static boolean isBase(RenderType type) {
        return type == ITEM_SHEET || type == BLOCK_CUTOUT || type == BLOCK_TRANSLUCENT;
    }

    public static boolean isGlint(RenderType type) {
        return type == ITEM_GLINT || type == ITEM_GLINT_TRANSLUCENT;
    }

    private static boolean isTranslucentBase(RenderType type) {
        return type == ITEM_SHEET || type == BLOCK_TRANSLUCENT;
    }

    public static List<RenderType> all() {
        return List.of(ITEM_SHEET, BLOCK_CUTOUT, BLOCK_TRANSLUCENT, ITEM_GLINT, ITEM_GLINT_TRANSLUCENT);
    }

    public static void ensureInitialized() {
        LoggerFactory.getLogger("enchantments_indicator").debug("[EI] the pipeline is registered before the renderBuffers");
    }
}