package ru.harimasa.ei.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
	private static final int BLAST_PROTECTION_COLOR = 0xFFFF3030;

	@ModifyArg(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", ordinal = 0), index = 6)
	private int changeArmorColor(int defaultColor, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) EquipmentClientInfo.LayerType layerType, @Local(argsOnly = true) Object object) {
		if (!(object instanceof AvatarRenderState)) return defaultColor;

		boolean isArmor = layerType == EquipmentClientInfo.LayerType.HUMANOID || layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS;
		if (!isArmor) {
			return defaultColor;
		}
		return hasBlastProtection(itemStack) ? BLAST_PROTECTION_COLOR : defaultColor;
	}

	private static boolean hasBlastProtection(ItemStack stack) {
		ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
		if (enchantments == null || enchantments.isEmpty()) return false;

		for (Holder<Enchantment> enchantment : enchantments.keySet()) {
			if (enchantment.unwrapKey().filter(Enchantments.BLAST_PROTECTION::equals).isPresent()) return enchantments.getLevel(enchantment) > 0;
		}
		return false;
	}
}