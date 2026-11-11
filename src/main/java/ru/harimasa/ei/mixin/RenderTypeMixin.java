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
import ru.harimasa.ei.config.enums.RenderMode;
import ru.harimasa.ei.render.EIGlintShaders;
import ru.harimasa.ei.render.EIItemRenderTypes;

@Mixin(RenderType.class)
public abstract class RenderTypeMixin {
	@ModifyArg(method = "draw(Lcom/mojang/blaze3d/vertex/MeshData;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DynamicUniforms;writeTransform(Lorg/joml/Matrix4fc;Lorg/joml/Vector4fc;Lorg/joml/Vector3fc;Lorg/joml/Matrix4fc;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"), index = 1)
	private Vector4fc eiColorModulator(Vector4fc original) {
		RenderType self = (RenderType) (Object) this;
		if (self == EIGlintShaders.ARMOR_GLINT) {
			EIConfig config = ConfigManager.get();
			ColorConfig to = config.colors.gradient ? config.colors.gradientTo : config.colors.enchantmentColor;
			return new Vector4f(to.r, to.g, to.b, Math.clamp(config.colors.intensity, 0.0f, 2.0f) * 0.5f);
		}

		if (EIItemRenderTypes.isGlint(self)) {
			EIConfig config = ConfigManager.get();
			if (!config.general.modEnabled || !config.inventory.enabled || config.inventory.renderMode != RenderMode.GLINT) return original;
			ColorConfig color = config.colors.enchantmentColor;
			float intensity = Math.clamp(config.colors.intensity, 0.0f, 2.0f);
			return new Vector4f((float) Math.sqrt(color.r) * intensity, (float) Math.sqrt(color.g) * intensity, (float) Math.sqrt(color.b) * intensity, 1.0f);
		}

		if (EIItemRenderTypes.isBase(self)) {
			EIConfig config = ConfigManager.get();
			if (!config.general.modEnabled || !config.inventory.enabled || config.inventory.renderMode != RenderMode.TEXTURE) return original;
			ColorConfig color = config.colors.enchantmentColor;
			return new Vector4f(color.r, color.g, color.b, 1.0f);
		}
		return original;
	}
}