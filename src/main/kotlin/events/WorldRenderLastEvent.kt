package moe.nea.firmod.events

import net.minecraft.client.renderer.MultiBufferSource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.state.level.CameraRenderState

/**
 * This event is called after all world rendering is done, but before any GUI rendering (including hand) has been done.
 */
data class WorldRenderLastEvent(
	val matrices: PoseStack,
	val tickCounter: Int,
	val camera: CameraRenderState,
	val vertexConsumers: MultiBufferSource.BufferSource,
) : FirmodEvent() {
	companion object : FirmodEventBus<WorldRenderLastEvent>()
}
