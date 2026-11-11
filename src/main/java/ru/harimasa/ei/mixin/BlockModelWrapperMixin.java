package ru.harimasa.ei.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.harimasa.ei.render.EIItemRenderTypes;

@Mixin(BlockModelWrapper.class)
public abstract class BlockModelWrapperMixin {
	@ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState$LayerRenderState;setRenderType(Lnet/minecraft/client/renderer/rendertype/RenderType;)V"), index = 0)
	private RenderType inventoryRenderType(RenderType original, @Local(argsOnly = true) ItemStackRenderState renderState, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) ItemDisplayContext displayContext) {
		RenderType replacement = EIItemRenderTypes.overrideBase(original, itemStack, displayContext);
		if (replacement != original) {
			renderState.appendModelIdentityElement(EIItemRenderTypes.identityKey());
		}
		return replacement;
	}
}