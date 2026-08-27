package moe.nea.firmod.util

import com.google.auto.service.AutoService
import kotlin.jvm.optionals.getOrNull
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import moe.nea.firmod.api.v1.FirmodAPI
import moe.nea.firmod.mixins.accessor.AccessorHandledScreen
import moe.nea.firmod.util.compatloader.CompatLoader

interface HoveredItemStackProvider : Comparable<HoveredItemStackProvider> {
	fun provideHoveredItemStack(screen: Screen): ItemStack?
	override fun compareTo(other: HoveredItemStackProvider): Int {
		return compareValues(this.prio, other.prio)
	}

	val prio: Int get() = 0

	companion object : CompatLoader<HoveredItemStackProvider>(HoveredItemStackProvider::class) {
		val sorted = HoveredItemStackProvider.allValidInstances.sorted()
	}
}

@AutoService(HoveredItemStackProvider::class)
class VanillaScreenProvider : HoveredItemStackProvider {

	override fun provideHoveredItemStack(screen: Screen): ItemStack? {
		if (screen !is AccessorHandledScreen) return null
		val vanillaSlot = screen.focusedSlot_Firmod?.item
		return vanillaSlot
	}

	override val prio: Int
		get() = -1
}

@AutoService(HoveredItemStackProvider::class)
class FirmodStackScreenProvider : HoveredItemStackProvider {
	override fun provideHoveredItemStack(screen: Screen): ItemStack? {
		return FirmodAPI.getInstance()
			.hoveredItemWidget
			.getOrNull()
			?.itemStack
	}
}

val Screen.focusedItemStack: ItemStack?
	get() =
		HoveredItemStackProvider.sorted
			.firstNotNullOfOrNull { it.provideHoveredItemStack(this)?.takeIf { !it.isEmpty } }
