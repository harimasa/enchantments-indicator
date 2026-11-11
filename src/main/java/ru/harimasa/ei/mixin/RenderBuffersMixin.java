package ru.harimasa.ei.mixin;

import java.util.SequencedMap;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.rendertype.RenderType;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ru.harimasa.ei.mixin.accessor.BufferSourceAccessor;
import ru.harimasa.ei.render.EIGlintShaders;

@Mixin(RenderBuffers.class)
public abstract class RenderBuffersMixin {
	@Shadow
	@Final
	private MultiBufferSource.BufferSource bufferSource;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void registerGlintBuffer(int useless, CallbackInfo ci) {
		SequencedMap<RenderType, ByteBufferBuilder> fixed = ((BufferSourceAccessor) this.bufferSource).ei$fixedBuffers();
		RenderType type = EIGlintShaders.ARMOR_GLINT;
		fixed.putIfAbsent(type, new ByteBufferBuilder(type.bufferSize()));
	}
}