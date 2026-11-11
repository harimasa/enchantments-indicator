package ru.harimasa.ei.mixin;

import java.util.SequencedMap;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiBufferSource.BufferSource.class)
public interface BufferSourceAccessor {

	@Accessor("fixedBuffers")
	SequencedMap<RenderType, ByteBufferBuilder> ei$fixedBuffers();
}