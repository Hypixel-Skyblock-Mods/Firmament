package moe.nea.firmod.events

import net.minecraft.world.item.ItemStack
import moe.nea.firmod.util.MC

sealed class ChestInventoryUpdateEvent : FirmodEvent() {
	companion object : FirmodEventBus<ChestInventoryUpdateEvent>()
	data class Single(val slot: Int, val stack: ItemStack) : ChestInventoryUpdateEvent()
	data class Multi(val contents: List<ItemStack>) : ChestInventoryUpdateEvent()
	val inventory = MC.screen
}
