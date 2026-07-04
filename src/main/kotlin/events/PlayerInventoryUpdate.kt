package moe.nea.firmod.events

import net.minecraft.world.item.ItemStack

sealed class PlayerInventoryUpdate : FirmodEvent() {
	companion object : FirmodEventBus<PlayerInventoryUpdate>()
	data class Single(val slot: Int, val stack: ItemStack) : PlayerInventoryUpdate() {
		override fun getOrNull(slot: Int): ItemStack? {
			if (slot == this.slot) return stack
			return null
		}

	}

	data class Multi(val contents: List<ItemStack>) : PlayerInventoryUpdate() {
		override fun getOrNull(slot: Int): ItemStack? {
			return contents.getOrNull(slot)
		}
	}

	abstract fun getOrNull(slot: Int): ItemStack?
}
