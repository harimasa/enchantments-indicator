package ru.harimasa.ei.mixin;

import net.minecraft.client.renderer.rendertype.RenderType;

import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.harimasa.ei.config.ColorConfig;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.render.EIGlintShaders;

@Mixin(RenderType.class)
public abstract class RenderTypeMixin {
	@ModifyArg(method = "draw(Lcom/mojang/blaze3d/vertex/MeshData;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DynamicUniforms;writeTransform(Lorg/joml/Matrix4fc;Lorg/joml/Vector4fc;Lorg/joml/Vector3fc;Lorg/joml/Matrix4fc;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"), index = 1)
	private Vector4fc glintGradientTo(Vector4fc original) {
		if ((Object) this != EIGlintShaders.ARMOR_GLINT) return original;
		EIConfig config = ConfigManager.get();
		ColorConfig to = config.colors.gradient ? config.colors.gradientTo : config.colors.enchantmentColor;
		return new Vector4f(to.r, to.g, to.b, Math.clamp(config.colors.intensity, 0.0f, 2.0f) * 0.5f);
	}
}