package ru.harimasa.ei.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.EquipmentClientInfo;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.RenderMode;
import ru.harimasa.ei.render.EIGlintShaders;
import ru.harimasa.ei.render.EnchantmentTint;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
	@ModifyArg(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", ordinal = 0), index = 6)
	private int textureTint(int defaultColor, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) EquipmentClientInfo.LayerType layerType, @Local(argsOnly = true) Object object) {
		EIConfig config = ConfigManager.get();
		if (config.colors.renderMode != RenderMode.TEXTURE) return defaultColor;
		if (!EnchantmentTint.applies(config, itemStack, layerType, object)) return defaultColor;
		return EnchantmentTint.color(config.colors.enchantmentColor, false);
	}

	@ModifyArg(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", ordinal = 1), index = 3)
	private RenderType glintRenderType(RenderType original, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) EquipmentClientInfo.LayerType layerType, @Local(argsOnly = true) Object object) {
		EIConfig config = ConfigManager.get();
		return glintActive(config, itemStack, layerType, object) ? EIGlintShaders.ARMOR_GLINT : original;
	}

	@ModifyArg(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", ordinal = 1), index = 6)
	private int glintColor(int defaultColor, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) EquipmentClientInfo.LayerType layerType, @Local(argsOnly = true) Object object) {
		EIConfig config = ConfigManager.get();
		if (!glintActive(config, itemStack, layerType, object)) return defaultColor;
		return EnchantmentTint.color(config.colors.gradient ? config.colors.gradientFrom : config.colors.enchantmentColor, false);
	}

	private static boolean glintActive(EIConfig config, ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Object object) {
		if (config.colors.renderMode != RenderMode.TINT) return false;
		return EnchantmentTint.applies(config, itemStack, layerType, object);
	}
}