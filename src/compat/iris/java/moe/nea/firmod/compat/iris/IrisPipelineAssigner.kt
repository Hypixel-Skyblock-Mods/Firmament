package moe.nea.firmod.compat.iris

import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.api.v0.IrisProgram
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.ClientInitEvent
import moe.nea.firmod.util.render.CustomRenderPipelines

object IrisPipelineAssigner {
	@Subscribe
	fun initIrisAssignments(event: ClientInitEvent) {
		val api = IrisApi.getInstance()
		api.assignPipeline(CustomRenderPipelines.GUI_TEXTURED_NO_DEPTH_TRIANGLES, IrisProgram.TEXTURED)
		api.assignPipeline(CustomRenderPipelines.OMNIPRESENT_LINES, IrisProgram.LINES)
		api.assignPipeline(CustomRenderPipelines.COLORED_OMNIPRESENT_QUADS, IrisProgram.BASIC)
		api.assignPipeline(CustomRenderPipelines.CIRCLE_FILTER_TRANSLUCENT_GUI_TRIANGLES, IrisProgram.TEXTURED)
		api.assignPipeline(CustomRenderPipelines.GUI_TEXTURED_NO_DEPTH_TRIANGLES_CIRCLE, IrisProgram.TEXTURED)
		api.assignPipeline(CustomRenderPipelines.PARALLAX_CAPE, IrisProgram.ENTITIES)
	}
}
