package moe.nea.firmod.util.render;

import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.UniformType;

public class CustomBindGroupLayouts {
	public static final BindGroupLayout ANIMATION_DATA = BindGroupLayout.builder()
		.withUniform("Animation", UniformType.UNIFORM_BUFFER)
		.build();
}
