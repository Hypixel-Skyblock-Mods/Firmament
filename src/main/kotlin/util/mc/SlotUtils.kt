package moe.nea.firmod.util.mc

// Container protocol buttons are 0/1/2, independent of SDL input button IDs.

import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.ContainerInput
import moe.nea.firmod.util.MC

object SlotUtils {
	fun Slot.clickMiddleMouseButton(handler: AbstractContainerMenu) {
		MC.interactionManager?.handleContainerInput(
			handler.containerId,
			this.index,
			2,
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
			1,
			ContainerInput.PICKUP,
			MC.player!!
		)
	}

	fun Slot.clickLeftMouseButton(handler: AbstractContainerMenu) {
		MC.interactionManager?.handleContainerInput(
			handler.containerId,
			this.index,
			0,
			ContainerInput.PICKUP,
			MC.player!!
		)
	}
}
