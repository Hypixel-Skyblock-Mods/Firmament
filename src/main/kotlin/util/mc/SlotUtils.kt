package moe.nea.firmament.util.mc

import org.lwjgl.glfw.GLFW
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.ContainerInput
import moe.nea.firmament.util.MC

object SlotUtils {
	fun Slot.clickMiddleMouseButton(handler: AbstractContainerMenu) {
		MC.interactionManager?.handleContainerInput(
			handler.containerId,
			this.index,
			GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
			ContainerInput.CLONE,
			MC.player!!
		)
	}

	fun Slot.swapWithHotBar(handler: AbstractContainerMenu, hotbarIndex: Int) {
		MC.interactionManager?.handleContainerInput(
			handler.containerId, this.index,
			hotbarIndex, ContainerInput.SWAP,
			MC.player!!
		)
	}

	fun Slot.clickRightMouseButton(handler: AbstractContainerMenu) {
		MC.interactionManager?.handleContainerInput(
			handler.containerId,
			this.index,
			GLFW.GLFW_MOUSE_BUTTON_RIGHT,
			ContainerInput.PICKUP,
			MC.player!!
		)
	}

	fun Slot.clickLeftMouseButton(handler: AbstractContainerMenu) {
		MC.interactionManager?.handleContainerInput(
			handler.containerId,
			this.index,
			GLFW.GLFW_MOUSE_BUTTON_LEFT,
			ContainerInput.PICKUP,
			MC.player!!
		)
	}
}
