package ru.harimasa.ei.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.harimasa.ei.render.EIItemRenderTypes;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
	private static RenderType glint(@Local(argsOnly = true) RenderType base) {
		return EIItemRenderTypes.glintFor(base, RenderTypes.glint());
	}

	@Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glintTranslucent()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
	private static RenderType glintTranslucent(@Local(argsOnly = true) RenderType base) {
		return EIItemRenderTypes.glintFor(base, RenderTypes.glintTranslucent());
	}
}