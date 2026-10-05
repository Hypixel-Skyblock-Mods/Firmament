package moe.nea.firmod.util.render;

import java.util.Optional;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import moe.nea.firmod.Firmod;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;

public class CustomRenderPipelines {
	public static final RenderPipeline GUI_TEXTURED_NO_DEPTH_TRIANGLES = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
		.withLocation(Firmod.identifier("gui_textured_overlay_tris"))
		.withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
		.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
		.withDepthStencilState(Optional.empty())
		.withCull(false)
		.build();
	public static final RenderPipeline GUI_TEXTURED_NO_DEPTH_TRIANGLES_CIRCLE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
		.withLocation(Firmod.identifier("gui_textured_overlay_tris"))
		.withVertexBinding(0, CustomVertexFormats.POSITION_TEX_COLOR_RADIUS)
		.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
		.withDepthStencilState(Optional.empty())
		.withCull(false)
		.build();
	public static final RenderPipeline COLORED_OMNIPRESENT_QUADS = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
		.withLocation(Firmod.identifier("colored_omnipresent_quads"))
		.withVertexShader("core/position_color")
		.withFragmentShader("core/position_color")
		.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
		.withPrimitiveTopology(PrimitiveTopology.QUADS)
		.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
		.withDepthStencilState(Optional.empty())
		.withCull(false)
		.build();
	public static final RenderPipeline CIRCLE_FILTER_TRANSLUCENT_GUI_TRIANGLES = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
		.withLocation(Firmod.identifier("gui_textured_overlay_tris_circle"))
		.withVertexShader(Firmod.identifier("circle_discard_color"))
		.withFragmentShader(Firmod.identifier("circle_discard_color"))
		.withVertexBinding(0, CustomVertexFormats.POSITION_TEX_COLOR_RADIUS)
		.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
		.build();
	public static final RenderPipeline PARALLAX_CAPE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
		.withLocation(Firmod.identifier("parallax_cape"))
		.withFragmentShader(Firmod.identifier("cape/parallax"))
		.withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1_SAMPLER2)
		.withBindGroupLayout(CustomBindGroupLayouts.ANIMATION_DATA)
		.build();
	public static final RenderPipeline OMNIPRESENT_LINES = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
		.withLocation(Firmod.identifier("omnipresent_lines"))
		.withDepthStencilState(Optional.empty())
		.build();
}
