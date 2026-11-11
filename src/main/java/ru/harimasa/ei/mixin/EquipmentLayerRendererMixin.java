package ru.harimasa.ei.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.harimasa.ei.config.ColorConfig;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.EnchantmentOption;
import ru.harimasa.ei.config.RenderMode;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {

	@ModifyArg(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", ordinal = 0), index = 6)
	private int changeArmorColor(int defaultColor, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) EquipmentClientInfo.LayerType layerType, @Local(argsOnly = true) Object object) {
		EIConfig config = ConfigManager.get();
		if (!config.general.modEnabled) return defaultColor;
		if (!(object instanceof AvatarRenderState)) return defaultColor;

		boolean isArmor = layerType == EquipmentClientInfo.LayerType.HUMANOID || layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS;
		if (!isArmor) {
			return defaultColor;
		}
		EnchantmentOption selected = EnchantmentOption.byIndex(config.colors.enchantment);
		if (!hasEnchantment(itemStack, selected)) return defaultColor;

		return tintColor(config);
	}

	private static boolean hasEnchantment(ItemStack stack, EnchantmentOption option) {
		ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
		if (enchantments == null || enchantments.isEmpty()) return false;

		for (Holder<Enchantment> enchantment : enchantments.keySet()) {
			if (enchantment.unwrapKey().filter(option.key()::equals).isPresent()) {
				return enchantments.getLevel(enchantment) > 0;
			}
		}
		return false;
	}

	private static int tintColor(EIConfig config) {
		ColorConfig color = config.colors.enchantmentColor;
		int alpha = config.colors.renderMode == RenderMode.TINT ? toChannel(color.a) : 255;

		return (alpha << 24) | (toChannel(color.r) << 16) | (toChannel(color.g) << 8) | toChannel(color.b);
	}

	private static int toChannel(float value) {
		return Math.max(0, Math.min(255, Math.round(value * 255.0f)));
	}
}