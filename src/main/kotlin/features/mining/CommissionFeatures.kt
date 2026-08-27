package moe.nea.firmod.features.mining

import moe.nea.firmod.Firmod
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.SlotRenderEvents
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.data.Config
import moe.nea.firmod.util.data.ManagedConfig
import moe.nea.firmod.util.mc.accessor
import moe.nea.firmod.util.mc.loreAccordingToNbt
import moe.nea.firmod.util.unformattedString

object CommissionFeatures {
	@Config
	object TConfig : ManagedConfig("commissions", Category.MINING) {
		val highlightCompletedCommissions by toggle("highlight-completed") { true }
	}


	@Subscribe
	fun onSlotRender(event: SlotRenderEvents.Before) {
		if (!TConfig.highlightCompletedCommissions) return
		if (MC.screenName != "Commissions") return
		val stack = event.slot.item
		if (stack.accessor().loreAccordingToNbt.any { it.unformattedString == "COMPLETED" }) {
			event.highlight(Firmod.identifier("completed_commission_background"))
		}
	}
}
