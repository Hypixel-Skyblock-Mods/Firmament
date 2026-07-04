package moe.nea.firmod.events

import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.ItemStack
import net.minecraft.world.inventory.Slot
import moe.nea.firmod.util.CommonSoundEffects
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.grey
import moe.nea.firmod.util.hover
import moe.nea.firmod.util.red
import moe.nea.firmod.util.tr

data class IsSlotProtectedEvent(
    val slot: Slot?,
    val actionType: ContainerInput,
    var isProtected: Boolean,
    val itemStackOverride: ItemStack?,
    val origin: MoveOrigin,
    var silent: Boolean = false,
) : FirmodEvent() {
	val itemStack get() = itemStackOverride ?: slot!!.item

	fun protect() {
		if (!isProtected) {
			silent = false
		}
		isProtected = true
	}

	fun protectSilent() {
		if (!isProtected) {
			silent = true
		}
		isProtected = true
	}

	enum class MoveOrigin {
		DROP_FROM_HOTBAR,
		SALVAGE,
		INVENTORY_MOVE
		;
	}

	companion object : FirmodEventBus<IsSlotProtectedEvent>() {
		@JvmStatic
		@JvmOverloads
		fun shouldBlockInteraction(
            slot: Slot?, action: ContainerInput,
            origin: MoveOrigin,
            itemStackOverride: ItemStack? = null,
		): Boolean {
			if (slot == null && itemStackOverride == null) return false
			val event = IsSlotProtectedEvent(slot, action, false, itemStackOverride, origin)
			publish(event)
			if (event.isProtected && !event.silent) {
				MC.sendChat(tr("firmod.protectitem", "Firmod protected your item: ${event.itemStack.hoverName}.\n")
					            .red()
					            .append(tr("firmod.protectitem.hoverhint", "Hover for more info.").grey())
					            .hover(tr("firmod.protectitem.hint",
					                      "To unlock this item use the Lock Slot or Lock Item keybind from Firmod while hovering over this item. If this is a bound slot, you can use disable the Lock Bound Slots setting.")))
				CommonSoundEffects.playFailure()
			}
			return event.isProtected
		}
	}
}
